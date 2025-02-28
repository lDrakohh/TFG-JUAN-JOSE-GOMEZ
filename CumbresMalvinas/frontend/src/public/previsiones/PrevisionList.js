import { useState, useEffect } from "react";
import { Button, Input, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function PrevisionList() {
  const [previsiones, setPrevisiones] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [registros, setRegistros] = useState({});
  const [nuevosRegistros, setNuevosRegistros] = useState({});

  //Cargar previsiones del día actual
  const cargarPrevisiones = () => {
    fetch(`/api/v1/previsiones/hoy`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then((data) => setPrevisiones(data))
      .catch((err) => console.error("Error cargando previsiones:", err));
  };

  useEffect(() => {
    cargarPrevisiones();
  }, []);

  //Cargar empresas
  useEffect(() => {
    fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then((data) => setEmpresas(data))
      .catch((err) => console.error("Error cargando empresas:", err));
  }, []);

  //Cargar registros por empresa
  const cargarRegistros = () => {
    if (empresas.length > 0) {
      const fetchRegistros = async () => {
        const nuevosRegistros = {};
        for (const empresa of empresas) {
          try {
            const res = await fetch(`/api/v1/registros/empresa/${empresa.id}/hoy`, { headers: { Authorization: `Bearer ${jwt}` } });
            const data = await res.json();
            nuevosRegistros[empresa.id] = Array.isArray(data) ? data : [];
          } catch (error) {
            console.error(`Error cargando registros de la empresa ${empresa.id}:`, error);
            nuevosRegistros[empresa.id] = [];
          }
        }
        setRegistros(nuevosRegistros);
      };
      fetchRegistros();
    }
  };

  useEffect(() => {
    cargarRegistros();
  }, [empresas]);

  const registrarMovimiento = async (previsionId) => {
    const cantidad = nuevosRegistros[previsionId];

    if (!cantidad || cantidad <= 0) {
      alert("Por favor, ingrese una cantidad válida.");
      return;
    }

    try {
      const response = await fetch(`/api/v1/registros/${previsionId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`,
        },
        body: JSON.stringify({ cantidadTraida: cantidad }),
      });

      if (!response.ok) {
        throw new Error("Error al registrar el movimiento");
      }

      setNuevosRegistros((prev) => ({ ...prev, [previsionId]: "" }));

      //Recargar previsiones y registros
      cargarPrevisiones();
      cargarRegistros();
    } catch (error) {
      console.error("Error registrando movimiento:", error);
    }
  };

  const eliminarRegistro = async (registroId, empresaId) => {
    try {
      const response = await fetch(`/api/v1/registros/${registroId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!response.ok) {
        throw new Error("Error al eliminar el registro");
      }
      cargarPrevisiones();
      cargarRegistros();
    } catch (error) {
      console.error("Error eliminando registro:", error);
    }
};

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Previsiones y Registros</h1>

      {empresas.length === 0 ? (
        <p className="text-center">Cargando empresas...</p>
      ) : (
        empresas.map((empresa) => (
          <div key={empresa.id} className="empresa-section">
            <h3>{empresa.nombreEmpresa}</h3>
            <div className="prevision-section">
              <h4>Previsiones</h4>
              {previsiones.length === 0 ? (
                <p>No hay previsiones registradas.</p>
              ) : (
                <Table striped>
                  <thead>
                    <tr>
                      <th>Tipo</th>
                      <th>Previsto</th>
                      <th>Traídas</th>
                      <th>Faltan</th>
                      <th>Registrar</th>
                    </tr>
                  </thead>
                  <tbody>
                    {previsiones
                      .filter((p) => p.empresa.id === empresa.id)
                      .map((prevision) => (
                        <tr key={prevision.id}>
                          <td>{prevision.fruta?.envase?.nombre || "N/A"}</td>
                          <td>{prevision.previsto}</td>
                          <td>{prevision.prevTraidas}</td>
                          <td>{prevision.prevFaltantes}</td>
                          <td>
                            <Input
                              type="number"
                              value={nuevosRegistros[prevision.id] || ""}
                              onChange={(e) => setNuevosRegistros({ ...nuevosRegistros, [prevision.id]: e.target.value })}
                              placeholder="Cantidad"
                            />
                            <Button size="sm" color="primary" onClick={() => registrarMovimiento(prevision.id)}>
                              Registrar
                            </Button>
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </Table>
              )}
            </div>
            <div className="registro-section">
              <h4>Registro de Movimientos</h4>
              {Array.isArray(registros[empresa.id]) && registros[empresa.id].length > 0 ? (
                <Table striped>
                  <thead>
                    <tr>
                      <th>Fecha</th>
                      <th>Tipo</th>
                      <th>Cantidad</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {registros[empresa.id].map((registro) => (
                      <tr key={registro.id}>
                        <td>{registro.fecha}</td>
                        <td>{registro.prevision?.fruta?.envase?.nombre || "N/A"}</td>
                        <td>{registro.cantidadTraida}</td>
                        <td>
                          <Button size="sm" color="danger" onClick={() => eliminarRegistro(registro.id, empresa.id)}>
                            🗑️
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              ) : (
                <p>No hay registros disponibles.</p>
              )}
            </div>
          </div>
        ))
      )}
    </div>
  );
}
