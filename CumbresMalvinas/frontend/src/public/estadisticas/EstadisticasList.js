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

    const getChartData = () => {
        if (!estadisticas) return {};

        const previsiones = estadisticas.previsiones || {};
        const registros = estadisticas.registros || {};
        const fechasDeSemana = estadisticas.fechasDeSemana || {};

        console.log("Previsiones:", previsiones); // Verifica que previsiones tenga datos
        console.log("Registros:", registros); // Verifica que registros tenga datos
        console.log("Fechas de semana:", fechasDeSemana); // Verifica las fechas de semana

        // Obtener las semanas (claves del objeto)
        const semanas = Object.keys(fechasDeSemana);
        if (semanas.length === 0) {
            return {};
        }

        // Crear los datasets para previsiones y registros
        const datasetsPrevisiones = [];
        const datasetsRegistros = [];

        // Iterar sobre las empresas para crear los datasets
        for (const empresa of Object.keys(previsiones)) {
            // Previsiones
            const previsionesPorSemana = semanas.map(semana => previsiones[empresa][semana] || 0);
            datasetsPrevisiones.push({
                label: `${empresa} - Previsiones`,
                data: previsionesPorSemana,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
            });

            // Registros
            const registrosPorSemana = semanas.map(semana => registros[empresa][semana] || 0);
            datasetsRegistros.push({
                label: `${empresa} - Registros`,
                data: registrosPorSemana,
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgba(153, 102, 255, 1)',
                borderWidth: 1,
            });
        }

        return {
            labels: semanas.map(semana => fechasDeSemana[semana]), // Usar las fechas de la semana como labels
            datasets: [...datasetsPrevisiones, ...datasetsRegistros],
        };
    };

    return (
        <div className="admin-page-container">
            <h1 className="text-center">Estadísticas de Previsiones y Registros por Semana</h1>
            <div className="chart-container" style={{ width: '100%', height: '500px' }}> {/* Ajustamos el tamaño del contenedor */}
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
                                        autoSkip: false, // Aseguramos que las etiquetas de las semanas no se omitan
                                        maxRotation: 90, // Rotamos las etiquetas para evitar que se corten
                                        minRotation: 0, // No rotamos mucho
                                    },
                                },
                            },
                        }}
                        height={400}  // Esta propiedad ajusta la altura del gráfico
                    />
                ) : (
                    <p>Cargando estadísticas...</p>
                )}
            </div>
        </div>
    );
}
