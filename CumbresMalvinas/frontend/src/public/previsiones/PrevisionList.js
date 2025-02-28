import { useState, useEffect } from "react";
import { Button, Input, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function PrevisionList() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [previsiones, setPrevisiones] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [nuevasPrevisiones, setNuevasPrevisiones] = useState({});
  const [nuevosRegistros, setNuevosRegistros] = useState({});
  const [registros, setRegistros] = useState({}); // Registros por empresa

  // Cargar previsiones
  useEffect(() => {
    fetch(`/api/v1/previsiones`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setPrevisiones);
  }, []);

  // Cargar empresas
  useEffect(() => {
    fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setEmpresas);
  }, []);

  // Cargar registros por empresa
  useEffect(() => {
    empresas.forEach((empresa) => {
      fetch(`/api/v1/registros/empresa/${empresa.id}`, { headers: { Authorization: `Bearer ${jwt}` } })
        .then((res) => res.json())
        .then((data) => {
          setRegistros((prev) => ({ ...prev, [empresa.id]: data }));
        });
    });
  }, [empresas]);

  function handlePrevisionChange(empresaId, value) {
    setNuevasPrevisiones({ ...nuevasPrevisiones, [empresaId]: value });
  }

  function handleRegistroChange(previsionId, value) {
    setNuevosRegistros({ ...nuevosRegistros, [previsionId]: value });
  }

  function handleSubmitPrevision(empresaId) {
    const cantidad = nuevasPrevisiones[empresaId] || 0;
    fetch(`/api/v1/previsiones`, {
      method: "POST",
      headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" },
      body: JSON.stringify({ empresaId, cantidad }),
    }).then(() => window.location.reload());
  }

  function handleDeleteRegistro(registroId, empresaId) {
    if (!window.confirm("¿Seguro que deseas eliminar este registro?")) return;
  
    fetch(`/api/v1/registros/${registroId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${jwt}` },
    })
      .then(() => {
        // Actualizar registros
        return fetch(`/api/v1/registros/empresa/${empresaId}`, { headers: { Authorization: `Bearer ${jwt}` } });
      })
      .then((res) => res.json())
      .then((data) => {
        setRegistros((prev) => ({ ...prev, [empresaId]: data }));
  
        // Actualizar previsiones después de eliminar registros
        return fetch("/api/v1/previsiones", { headers: { Authorization: `Bearer ${jwt}` } });
      })
      .then((res) => res.json())
      .then(setPrevisiones)
      .catch((error) => console.error("Error eliminando el registro:", error));
  }
  

  function handleSubmitRegistro(previsionId, empresaId) {
    const cantidadTraida = nuevosRegistros[previsionId] || 0;
  
    fetch(`/api/v1/registros/${previsionId}?cantidadTraida=${cantidadTraida}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${jwt}`, "Content-Type": "application/json" },
    })
      .then(() => {
        // Actualizar registros
        return fetch(`/api/v1/registros/empresa/${empresaId}`, { headers: { Authorization: `Bearer ${jwt}` } });
      })
      .then((res) => res.json())
      .then((data) => {
        setRegistros((prev) => ({ ...prev, [empresaId]: data }));
        
        //Actualizar previsiones después de modificar registros
        return fetch("/api/v1/previsiones", { headers: { Authorization: `Bearer ${jwt}` } });
      })
      .then((res) => res.json())
      .then(setPrevisiones)
      .catch((error) => console.error("Error registrando mercancía:", error));
  }
  

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Previsiones y Registros</h1>
      {empresas.map((empresa) => (
        <div key={empresa.id} className="empresa-section">
          <h3>{empresa.nombreEmpresa}</h3>

          {/* Sección de Previsiones */}
          <div className="prevision-section">
            <h4>Previsiones</h4>
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
                {previsiones.filter((p) => p.empresa.id === empresa.id).map((prevision) => (
                  <tr key={prevision.id}>
                    <td>{prevision.fruta.envase ? prevision.fruta.envase.nombre : "N/A"}</td>
                    <td>{prevision.previsto}</td>
                    <td>{prevision.prevTraidas}</td>
                    <td>{prevision.prevFaltantes}</td>
                    <td>
                      <Input
                        type="number"
                        value={nuevosRegistros[prevision.id] || ""}
                        onChange={(e) => handleRegistroChange(prevision.id, e.target.value)}
                        placeholder="Cantidad"
                      />
                      <Button size="sm" color="primary" onClick={() => handleSubmitRegistro(prevision.id, empresa.id)}>
                        Registrar
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>

          <h4>Añadir Previsión</h4>
          <Input
            type="number"
            value={nuevasPrevisiones[empresa.id] || ""}
            onChange={(e) => handlePrevisionChange(empresa.id, e.target.value)}
            placeholder="Cantidad"
          />
          <Button size="sm" color="success" onClick={() => handleSubmitPrevision(empresa.id)}>
            Añadir Previsión
          </Button>

          {/* Sección de Registros */}
          <div className="registro-section">
            <h4>Registro de Movimientos</h4>
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
                {(registros[empresa.id] || []).map((registro) => (
                  <tr key={registro.id}>
                    <td>{registro.fecha}</td>
                    <td>{registro.prevision.fruta.envase ? registro.prevision.fruta.envase.nombre : "N/A"}</td>
                    <td>{registro.cantidadTraida}</td>
                    <td>
                      <Button size="sm" color="danger" onClick={() => handleDeleteRegistro(registro.id, empresa.id)}>
                        🗑️
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        </div>
      ))}
    </div>
  );
}
