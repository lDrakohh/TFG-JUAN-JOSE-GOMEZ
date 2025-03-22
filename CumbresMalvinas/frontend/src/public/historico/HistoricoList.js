import React, { useState, useEffect } from "react";
import { Button, Input, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function HistoricoList() {
    const [empresas, setEmpresas] = useState([]);
    const [empresasSeleccionadas, setEmpresasSeleccionadas] = useState([]);
    const [fechaInicio, setFechaInicio] = useState("");
    const [fechaFin, setFechaFin] = useState("");
    const [historico, setHistorico] = useState({ previsiones: [], registros: [] });
    const [email, setEmail] = useState("");

    useEffect(() => {
        fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
            .then((res) => res.json())
            .then((data) => setEmpresas(data))
            .catch((err) => console.error("Error cargando empresas:", err));
    }, []);

    const handleCheckboxChange = (e, empresaId) => {
        setEmpresasSeleccionadas((prevSeleccionadas) =>
            e.target.checked
                ? [...prevSeleccionadas, empresaId]
                : prevSeleccionadas.filter(id => id !== empresaId)
        );
    };

    const buscarHistorico = async () => {
        if (empresasSeleccionadas.length === 0 || !fechaInicio || !fechaFin) {
            alert("Seleccione al menos una empresa y un rango de fechas");
            return;
        }

        if (new Date(fechaInicio) > new Date(fechaFin)) {
            alert("La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        try {
            const empresaIds = empresasSeleccionadas.join(",");
            const response = await fetch(
                `/api/v1/historico?empresas=${empresaIds}&inicio=${fechaInicio}&fin=${fechaFin}`,
                { headers: { Authorization: `Bearer ${jwt}` } }
            );
            if (!response.ok) throw new Error("Error obteniendo datos");
            const data = await response.json();

            setHistorico({
                previsiones: data.previsiones || [],
                registros: data.registros || []
            });
        } catch (error) {
            console.error("Error buscando histórico:", error);
        }
    };

    const enviarCorreo = async () => {
        if (!email) {
            alert("Ingrese una dirección de correo.");
            return;
        }

        if (historico.previsiones.length === 0 && historico.registros.length === 0) {
            alert("No hay datos para enviar.");
            return;
        }

        const contenido = `
            <h2>Histórico de Previsiones y Registros</h2>
            <table border="1" cellspacing="0" cellpadding="5">
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Empresa</th>
                        <th>Fruta</th>
                        <th>Previsto</th>
                        <th>Traído</th>
                    </tr>
                </thead>
                <tbody>
                    ${historico.previsiones?.map(prevision => `  
                        <tr>
                            <td>${prevision.fecha}</td>
                            <td>${prevision.empresa?.nombreEmpresa || "N/A"}</td>
                            <td>${prevision.fruta?.variedad || "N/A"}</td>
                            <td>${prevision.previsto}</td>
                            <td>-</td>
                        </tr>
                        ${historico.registros?.filter(registro => registro.prevision?.id === prevision.id)
                .map(registro => `
                        <tr>
                            <td>↳ ${registro.fecha}</td>
                            <td>${registro.prevision?.empresa?.nombreEmpresa || "N/A"}</td>
                            <td>${registro.prevision?.fruta?.variedad || "N/A"}</td>
                            <td>-</td>
                            <td>${registro.cantidadTraida}</td>
                        </tr>`).join('')}
                    `).join('')}
                </tbody>
            </table>
        `;

        try {
            const response = await fetch("/api/v1/historico/enviar-correo", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${jwt}`,
                },
                body: JSON.stringify({ email, contenido }),
            });

            const result = await response.text();
            alert(result);
        } catch (error) {
            console.error("Error enviando correo:", error);
            alert("Error enviando correo.");
        }
    };

    return (
        <div className="admin-page-container">
            <h1 className="text-center">Histórico de Previsiones y Registros</h1>
            <div className="filters">
                <div className="empresa-checkboxes">
                    {empresas.map((emp) => (
                        <label key={emp.id} className="checkbox-label">
                            <input
                                type="checkbox"
                                value={emp.id}
                                checked={empresasSeleccionadas.includes(emp.id)}
                                onChange={(e) => handleCheckboxChange(e, emp.id)}
                            />
                            {emp.nombreEmpresa}
                        </label>
                    ))}
                </div>
                <Input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} />
                <Input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} />
                <Button color="primary" onClick={buscarHistorico}>Buscar</Button>
            </div>
            <Table striped>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Empresa</th>
                        <th>Fruta</th>
                        <th>Previsto</th>
                        <th>Traído</th>
                    </tr>
                </thead>
                <tbody>
                    {historico.previsiones.length > 0 ? (
                        historico.previsiones.map(prevision => (
                            <React.Fragment key={prevision.id}>
                                {/* Mostrar la previsión */}
                                <tr>
                                    <td>{prevision.fecha}</td>
                                    <td>{prevision.empresa?.nombreEmpresa || "N/A"}</td>
                                    <td>{prevision.fruta?.variedad || "N/A"}</td>
                                    <td>{prevision.previsto}</td>
                                    <td>-</td>
                                </tr>

                                {/* Mostrar los registros asociados a la previsión */}
                                {historico.registros
                                    .filter(registro => registro.prevision?.id === prevision.id)
                                    .map(registro => (
                                        <tr key={registro.id}>
                                            <td>↳ {registro.fecha}</td>
                                            <td>{registro.prevision?.empresa?.nombreEmpresa || "N/A"}</td>
                                            <td>{registro.prevision?.fruta?.variedad || "N/A"}</td>
                                            <td>-</td>
                                            <td>{registro.cantidadTraida}</td>
                                        </tr>
                                    ))}
                            </React.Fragment>
                        ))
                    ) : (
                        <tr><td colSpan="5" className="text-center">No hay datos</td></tr>
                    )}
                </tbody>
            </Table>
            {historico.previsiones.length > 0 && (
                <div className="email-section">
                    <Input
                        type="email"
                        placeholder="Ingrese el correo"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <Button color="success" onClick={enviarCorreo}>Enviar por Correo</Button>
                </div>
            )}
        </div>
    );
}
