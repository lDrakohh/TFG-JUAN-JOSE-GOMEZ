import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function FrutaEditAdmin() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [fruta, setFruta] = useState({
    variedad: "",
    calidad: "",
    marca: "",
    envaseId: ""
  });
  const [envases, setEnvases] = useState([]);

  useEffect(() => {
    // Si es una edición, obtener la fruta existente
    if (id) {
      fetch(`/api/v1/frutas/${id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        }
      })
        .then((res) => res.json())
        .then(setFruta);
    }

    fetch(`/api/v1/envases`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      }
    })
      .then((res) => res.json())
      .then(setEnvases);
  }, [id]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFruta({ ...fruta, [name]: value });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const method = id ? "PUT" : "POST"; // Si hay id, es un update (PUT), si no, es un create (POST)
    const url = id ? `/api/v1/frutas/${id}` : `/api/v1/frutas`; // Cambiar la URL según si es edición o creación

    fetch(url, {
      method: method,
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(fruta),
    })
      .then(() => navigate("/frutas"));
  };

  return (
    <div className="auth-page-container">
      <h2>{id ? "Editar Fruta" : "Crear Fruta"}</h2>
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <Label>Variedad</Label>
          <Input
            type="text"
            name="variedad"
            value={fruta.variedad}
            onChange={handleChange}
            required
          />
          <Label>Calidad</Label>
          <Input
            type="text"
            name="calidad"
            value={fruta.calidad}
            onChange={handleChange}
            required
          />
          <Label>Marca</Label>
          <Input
            type="text"
            name="marca"
            value={fruta.marca}
            onChange={handleChange}
            required
          />
          <Label>Envase</Label>
          <Input
            type="select"
            name="envaseId"
            value={fruta.envaseId}
            onChange={handleChange}
            required
          >
            <option value="">Seleccione un envase</option>
            {envases.map((envase) => (
              <option key={envase.id} value={envase.id}>
                {envase.nombre} - {envase.pesoGramos} gramos
              </option>
            ))}
          </Input>
          <div className="custom-button-row">
            <Button type="submit" className="auth-button">
              {id ? "Actualizar" : "Crear"}
            </Button>
            <Link to="/frutas" className="auth-button">
              Cancelar
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
