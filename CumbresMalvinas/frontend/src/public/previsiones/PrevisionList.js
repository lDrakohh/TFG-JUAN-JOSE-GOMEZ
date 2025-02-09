import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, Input } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function PrevisionList() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [previsiones, setPrevisiones] = useFetchState(
    [],
    `/api/v1/previsiones`,
    jwt,
    setMessage,
    setVisible
  );
  const [registros, setRegistros] = useState({});
  const [empresas, setEmpresas] = useState([]);

  useEffect(() => {
    // Cargar las empresas desde el backend
    fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setEmpresas);
  }, []);

  function handleRegistroChange(previsionId, empresaId, value) {
    setRegistros({ ...registros, [empresaId]: { ...registros[empresaId], [previsionId]: value } });
  }

  function handleSubmitRegistro(previsionId, empresaId) {
    const cantidadTraida = registros[empresaId]?.[previsionId] || 0;
    fetch(`/api/v1/registros`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        previsionId,
        cantidadTraida,
        fecha: new Date().toISOString().split("T")[0],
      }),
    }).then(() => {
      window.location.reload();
    });
  }

  const addPrevision = (empresaId, fruta) => {
    const nuevaPrevision = {
      empresa: { id: empresaId },
      fruta: fruta,
      previsto: 0, // Inicialmente vacío
      prevTraidas: 0, // Inicialmente vacío
      prevFaltantes: fruta.envase ? fruta.envase.pesoGramos : 0, // Valor inicial basado en el envase
      fecha: new Date().toISOString().split("T")[0], // Fecha actual
    };

    fetch(`/api/v1/previsiones`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(nuevaPrevision),
    }).then(() => {
      window.location.reload(); // Recargar la página para ver la previsión añadida
    }).catch((error) => {
      console.error("Error al añadir la previsión:", error);
    });
  };

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Previsiones</h1>
      <Button color="success" tag={Link} to="/previsiones/new">
        Añadir Previsión
      </Button>

      {empresas.map((empresa) => (
        <div key={empresa.id} className="empresa-section">
          <h3>{empresa.nombreEmpresa}</h3>
          
          {/* Añadir previsión */}
          <Button
            size="sm"
            color="primary"
            onClick={() => addPrevision(empresa.id, { nombre: "EXP - 5KGs", envase: { pesoGramos: 5000, nombre: "EXP - 5KGs" } })}
          >
            Añadir Previsión
          </Button>

          {/* Mostrar previsiones de cada empresa */}
          <div className="previsiones">
            {previsiones.filter(p => p.empresa.id === empresa.id).map((prevision) => (
              <div key={prevision.id} className="prevision-row">
                <div className="prevision-field">
                  <strong>Tipo</strong>
                  <span>{prevision.fruta.envase ? prevision.fruta.envase.nombre : "Tipo no definido"}</span>
                </div>
                <div className="prevision-field">
                  <strong>Previsto</strong>
                  <span>{prevision.previsto}</span>
                </div>
                <div className="prevision-field">
                  <strong>Traídas</strong>
                  <Input
                    type="number"
                    value={registros[empresa.id]?.[prevision.id] || ""}
                    onChange={(e) => handleRegistroChange(prevision.id, empresa.id, e.target.value)}
                    placeholder="Cantidad"
                  />
                </div>
                <div className="prevision-field">
                  <strong>Faltantes</strong>
                  <span>{prevision.prevFaltantes}</span>
                </div>
                <div className="prevision-field">
                  <Button
                    size="sm"
                    color="success"
                    onClick={() => handleSubmitRegistro(prevision.id, empresa.id)}
                  >
                    Registrar
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
