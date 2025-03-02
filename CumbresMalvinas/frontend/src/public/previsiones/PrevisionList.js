import { useState, useEffect } from "react";
import { Button, Input, Table, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function PrevisionList() {
  const [previsiones, setPrevisiones] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [frutas, setFrutas] = useState([]);
  const [registros, setRegistros] = useState({});
  const [nuevosRegistros, setNuevosRegistros] = useState({});
  const [modalOpen, setModalOpen] = useState(false);
  const [nuevaPrevision, setNuevaPrevision] = useState({ empresaId: "", frutaId: "", cantidad: "" });

  const cargarPrevisiones = () => {
    fetch(`/api/v1/previsiones/hoy`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then((data) => setPrevisiones(data))
      .catch((err) => console.error("Error cargando previsiones:", err));
  };

  useEffect(() => {
    cargarPrevisiones();
  }, []);

  useEffect(() => {
    fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then((data) => setEmpresas(data))
      .catch((err) => console.error("Error cargando empresas:", err));
  }, []);

  useEffect(() => {
    fetch(`/api/v1/frutas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then((data) => setFrutas(data))
      .catch((err) => console.error("Error cargando frutas:", err));
  }, []);

  const cargarRegistros = () => {
    if (empresas.length > 0) {
      const fetchRegistros = async () => {
        const nuevosRegistros = {};
        for (const empresa of empresas) {
          try {
            const res = await fetch(`/api/v1/registros/empresa/${empresa.id}/hoy`, {
              headers: { Authorization: `Bearer ${jwt}` }
            });
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

    console.log("Previsión ID:", previsionId);
    console.log("Cantidad ingresada:", cantidad);

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
        throw new Error(`Error al registrar el movimiento: ${response.statusText}`);
      }

      console.log("Registro agregado correctamente");

      setNuevosRegistros((prev) => ({ ...prev, [previsionId]: "" }));

      cargarPrevisiones();
      cargarRegistros();
    } catch (error) {
      console.error("Error registrando movimiento:", error);
    }
  };

  const eliminarRegistro = async (registroId) => {
    console.log("Intentando eliminar registro con ID:", registroId);

    try {
      const response = await fetch(`/api/v1/registros/${registroId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Error al eliminar el registro: ${response.statusText}`);
      }

      console.log("Registro eliminado correctamente");
      cargarPrevisiones();
      cargarRegistros();
    } catch (error) {
      console.error("Error eliminando registro:", error);
    }
  };


  const agregarPrevision = async () => {
    const empresaId = Number(nuevaPrevision.empresaId);
    const frutaId = Number(nuevaPrevision.frutaId);
    const previsto = Number(nuevaPrevision.cantidad);

    console.log({ empresaId, frutaId, previsto });

    if (!empresaId || !frutaId || !previsto) {
      alert("Todos los campos son obligatorios");
      return;
    }

    try {
      const response = await fetch(`/api/v1/previsiones`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwt}`,
        },
        body: JSON.stringify({
          empresaId,
          frutaId,
          previsto,
        }),
      });

      if (!response.ok) {
        throw new Error("Error al agregar la previsión");
      }

      setModalOpen(false);
      setNuevaPrevision({ empresaId: "", frutaId: "", cantidad: "" });
      cargarPrevisiones();
    } catch (error) {
      console.error("Error agregando previsión:", error);
    }
  };

  const eliminarPrevision = async (previsionId) => {
    console.log("Verificando si la previsión tiene registros...");

    try {
      const response = await fetch(`/api/v1/previsiones/${previsionId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Error al eliminar la previsión: ${response.statusText}`);
      }

      //Verificar si hay contenido antes de intentar parsearlo
      let data;
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        data = null; 
      }

      console.log("Previsión eliminada correctamente", data);

      cargarPrevisiones();
      cargarRegistros();
    } catch (error) {
      console.error("Error eliminando previsión:", error);
    }
  };


  return (
    <div className="admin-page-container">
      <h1 className="text-center">Previsiones y Registros</h1>

      <Button color="success" onClick={() => setModalOpen(true)}>➕ Agregar Previsión</Button>

      <Modal isOpen={modalOpen} toggle={() => setModalOpen(!modalOpen)}>
        <ModalHeader toggle={() => setModalOpen(!modalOpen)}>Nueva Previsión</ModalHeader>
        <ModalBody>
          <label>Empresa:</label>
          <Input type="select" value={nuevaPrevision.empresaId} onChange={(e) => setNuevaPrevision({ ...nuevaPrevision, empresaId: e.target.value })}>
            <option value="">Seleccione una empresa</option>
            {empresas.map((emp) => (
              <option key={emp.id} value={emp.id}>{emp.nombreEmpresa}</option>
            ))}
          </Input>

          <label>Tipo de Fruta:</label>
          <Input type="select" value={nuevaPrevision.frutaId} onChange={(e) => setNuevaPrevision({ ...nuevaPrevision, frutaId: e.target.value })}>
            <option value="">Seleccione una fruta</option>
            {frutas.map((fruta) => (
              <option key={fruta.id} value={fruta.id}>
                {fruta.calidad} - {fruta.variedad} ({fruta.envase?.nombre || "Sin envase"})
              </option>
            ))}
          </Input>

          <label>Cantidad Prevista:</label>
          <Input type="number" value={nuevaPrevision.cantidad} onChange={(e) => setNuevaPrevision({ ...nuevaPrevision, cantidad: e.target.value })} />

        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={agregarPrevision}>Agregar</Button>
          <Button color="secondary" onClick={() => setModalOpen(false)}>Cancelar</Button>
        </ModalFooter>
      </Modal>

      {empresas.map((empresa) => (
        <div key={empresa.id} className="empresa-section">
          <h3>{empresa.nombreEmpresa}</h3>
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
                  <td>{prevision.fruta?.envase?.nombre || "N/A"}</td>
                  <td>{prevision.previsto}</td>
                  <td>{prevision.prevTraidas}</td>
                  <td>{prevision.prevFaltantes}</td>
                  <td>
                    <Input
                      type="number"
                      placeholder="Cantidad"
                      value={nuevosRegistros[prevision.id] || ""}
                      onChange={(e) =>
                        setNuevosRegistros({ ...nuevosRegistros, [prevision.id]: e.target.value })
                      }
                    />
                    <Button
                      color="primary"
                      onClick={() => registrarMovimiento(prevision.id)}
                      disabled={!nuevosRegistros[prevision.id]}
                    >
                      Registrar
                    </Button>
                  </td>
                  <td>
                    <Button size="sm" color="danger" onClick={() => eliminarPrevision(prevision.id)}>
                      🗑️ Eliminar
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
            <tbody>
              {registros[empresa.id] && registros[empresa.id].length > 0 ? (
                registros[empresa.id].map((registro) => (
                  <tr key={registro.id}>
                    <td>{registro.fecha}</td>
                    <td>{registro.prevision?.fruta?.envase?.nombre || "N/A"}</td>
                    <td>{registro.cantidadTraida}</td>
                    <td>
                      <Button size="sm" color="danger" onClick={() => eliminarRegistro(registro.id)}>
                        🗑️
                      </Button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" className="text-center">No hay registros</td>
                </tr>
              )}
            </tbody>
          </Table>
        </div>
      ))}
    </div>
  );
}
