import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function RegistroMercancia() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [registro, setRegistro] = useState({
    previsionId: "",
    cantidadTraida: "",
    fecha: "",
  });
  const [previsiones, setPrevisiones] = useState([]);

  useEffect(() => {
    fetch(`/api/v1/previsiones`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setPrevisiones);
    if (id) {
      fetch(`/api/v1/registros/${id}`, { headers: { Authorization: `Bearer ${jwt}` } })
        .then((res) => res.json())
        .then(setRegistro);
    }
  }, [id]);

  function handleChange(event) {
    const { name, value } = event.target;
    setRegistro({ ...registro, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();
    fetch(`/api/v1/registros${id ? `/${id}` : ""}`, {
      method: id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(registro),
    }).then(() => navigate("/registros"));
  }

  return (
    <div className="auth-page-container">
      <h2>{id ? "Editar Registro de Mercancía" : "Añadir Registro de Mercancía"}</h2>
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <Label>Previsión</Label>
          <Input type="select" name="previsionId" value={registro.previsionId} onChange={handleChange} required>
            <option value="">Seleccione una previsión</option>
            {previsiones.map((p) => (
              <option key={p.id} value={p.id}>{`${p.empresa.nombreEmpresa} - ${p.fruta.nombre}`}</option>
            ))}
          </Input>
          <Label>Cantidad Traída</Label>
          <Input type="number" name="cantidadTraida" value={registro.cantidadTraida} onChange={handleChange} required />
          <Label>Fecha</Label>
          <Input type="date" name="fecha" value={registro.fecha} onChange={handleChange} required />
          <div className="custom-button-row">
            <Button type="submit" className="auth-button">Guardar</Button>
            <Link to="/registros" className="auth-button">Cancelar</Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
