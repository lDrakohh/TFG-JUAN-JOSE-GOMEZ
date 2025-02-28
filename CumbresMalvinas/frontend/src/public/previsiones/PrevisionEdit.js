import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function PrevisionEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [prevision, setPrevision] = useState({
    empresaId: "",
    frutaId: "",
    previsto: "",
    fecha: "",
  });
  const [empresas, setEmpresas] = useState([]);
  const [frutas, setFrutas] = useState([]);

  useEffect(() => {
    fetch(`/api/v1/empresas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setEmpresas);
    fetch(`/api/v1/frutas`, { headers: { Authorization: `Bearer ${jwt}` } })
      .then((res) => res.json())
      .then(setFrutas);
    if (id) {
      fetch(`/api/v1/previsiones/${id}`, { headers: { Authorization: `Bearer ${jwt}` } })
        .then((res) => res.json())
        .then(setPrevision);
    }
  }, [id]);

  function handleChange(event) {
    const { name, value } = event.target;
    setPrevision({ ...prevision, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();
    fetch(`/api/v1/previsiones${id ? `/${id}` : ""}`, {
      method: id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(prevision),
    }).then(() => navigate("/previsiones"));
  }

  return (
    <div className="auth-page-container">
      <h2>{id ? "Editar Previsión" : "Añadir Previsión"}</h2>
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <Label>Empresa</Label>
          <Input type="select" name="empresaId" value={prevision.empresaId} onChange={handleChange} required>
            <option value="">Seleccione una empresa</option>
            {empresas.map((e) => (
              <option key={e.id} value={e.id}>{e.nombreEmpresa}</option>
            ))}
          </Input>
          <Label>Fruta</Label>
          <Input type="select" name="frutaId" value={prevision.frutaId} onChange={handleChange} required>
            <option value="">Seleccione una fruta</option>
            {frutas.map((f) => (
              <option key={f.id} value={f.id}>{f.nombre}</option>
            ))}
          </Input>
          <Label>Cantidad Prevista</Label>
          <Input type="number" name="previsto" value={prevision.previsto} onChange={handleChange} required />
          <Label>Fecha</Label>
          <Input type="date" name="fecha" value={prevision.fecha} onChange={handleChange} required />
          <div className="custom-button-row">
            <Button type="submit" className="auth-button">Guardar</Button>
            <Link to="/previsiones" className="auth-button">Cancelar</Link>
          </div>
        </Form>
      </div>
    </div>
  );
}