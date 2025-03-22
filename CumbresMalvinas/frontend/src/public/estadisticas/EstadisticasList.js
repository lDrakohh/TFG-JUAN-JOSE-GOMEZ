import React, { useState, useEffect } from "react";
import { Bar } from "react-chartjs-2";
import { Button } from "reactstrap";
import tokenService from "../../services/token.service";
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const jwt = tokenService.getLocalAccessToken();

export default function EstadisticasList() {
    const [estadisticas, setEstadisticas] = useState(null);

    useEffect(() => {
        fetch('/api/v1/estadisticas/resumen', {
            headers: { Authorization: `Bearer ${jwt}` },
        })
            .then((res) => res.json())
            .then((data) => setEstadisticas(data))
            .catch((err) => console.error("Error obteniendo estadísticas:", err));
    }, []);

    const getRandomColor = () => {
        // Función para generar un color aleatorio
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    };

    const getChartData = () => {
        if (!estadisticas) return {};

        const previsiones = estadisticas.previsiones || {};
        const registros = estadisticas.registros || {};
        const fechasDeSemana = estadisticas.fechasDeSemana || {};

        console.log("Previsiones:", previsiones);
        console.log("Registros:", registros);
        console.log("Fechas de semana:", fechasDeSemana);

        const semanas = Object.keys(fechasDeSemana);
        if (semanas.length === 0) {
            return {};
        }

        const datasetsPrevisiones = [];
        const datasetsRegistros = [];

        for (const empresa of Object.keys(previsiones)) {
            // Generamos un color único para cada empresa
            const colorEmpresa = getRandomColor();

            // Previsiones
            const previsionesPorSemana = semanas.map(semana => previsiones[empresa][semana] || 0);
            datasetsPrevisiones.push({
                label: `${empresa} - Previsiones`,
                data: previsionesPorSemana,
                backgroundColor: colorEmpresa,
                borderColor: colorEmpresa,
                borderWidth: 1,
            });

            // Registros
            const registrosPorSemana = semanas.map(semana => registros[empresa][semana] || 0);
            datasetsRegistros.push({
                label: `${empresa} - Registros`,
                data: registrosPorSemana,
                backgroundColor: colorEmpresa,
                borderColor: colorEmpresa,
                borderWidth: 1,
            });
        }

        return {
            labels: semanas.map(semana => fechasDeSemana[semana]),
            datasets: [...datasetsPrevisiones, ...datasetsRegistros],
        };
    };

    return (
        <div className="admin-page-container">
            <h1 className="text-center">Estadísticas de Previsiones y Registros por Semana</h1>
            <div className="chart-container" style={{ width: '100%', height: '500px' }}>
                {estadisticas ? (
                    <Bar
                        data={getChartData()}
                        options={{
                            responsive: true,
                            maintainAspectRatio: false, // No mantener la proporción para que se ajuste al contenedor
                            plugins: {
                                title: {
                                    display: true,
                                    text: 'Resumen de Previsiones y Registros por Semana y Empresa',
                                },
                            },
                            scales: {
                                x: {
                                    ticks: {
                                        autoSkip: false,
                                        maxRotation: 90,
                                        minRotation: 0,
                                    },
                                },
                            },
                        }}
                        height={400}
                    />
                ) : (
                    <p>Cargando estadísticas...</p>
                )}
            </div>
        </div>
    );
}
