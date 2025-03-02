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
    const [historico, setHistorico] = useState([]);

    useEffect(() => {
        fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
            .then((res) => res.json())
            .then((data) => setEmpresas(data))
            .catch((err) => console.error("Error cargando empresas:", err));
    }, []);

    const handleCheckboxChange = (e, empresaId) => {
        setEmpresasSeleccionadas((prevSeleccionadas) =>
            e.target.checked
                ? [...prevSeleccionadas, empresaId] // Agregar si está marcado
                : prevSeleccionadas.filter(id => id !== empresaId) // Quitar si se desmarca
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

            setHistorico(data);
        } catch (error) {
            console.error("Error buscando histórico:", error);
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
                    {historico.previsiones && historico.previsiones.length > 0 ? (
                        historico.previsiones.map((prevision) => (
                            <React.Fragment key={`prevision-${prevision.id}`}>
                                <tr className="prevision-row">
                                    <td>{prevision.fecha}</td>
                                    <td>{prevision.empresa?.nombreEmpresa || "N/A"}</td>
                                    <td>{prevision.fruta?.variedad || "N/A"}</td>
                                    <td>{prevision.previsto}</td>
                                    <td>-</td>
                                </tr>
                                {historico.registros
                                    .filter((registro) => registro.prevision.id === prevision.id)
                                    .map((registro) => (
                                        <tr key={`registro-${registro.id}`} className="registro-row">
                                            <td className="registro-indent">↳ {registro.fecha}</td>
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
        </div>
    );
}
