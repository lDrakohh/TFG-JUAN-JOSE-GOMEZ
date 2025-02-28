import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";

const jwt = tokenService.getLocalAccessToken();

export default function EnvaseEditAdmin() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [envase, setEnvase] = useState({
    nombre: "",
    pesoGramos: ""
  });

  useEffect(() => {
    if (id) {
      fetch(`/api/v1/envases/${id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`
        }
      })
        .then((res) => res.json())
        .then(setEnvase);
    }
  }, [id]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setEnvase({ ...envase, [name]: value });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    fetch(`/api/v1/envases`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(envase),
    })
      .then((response) => response.json())
      .then((newEnvase) => {
        navigate("/envases");
      })
      .catch((error) => {
        console.error("Error al crear el envase:", error);
      });
};

  return (
    <div className="auth-page-container">
      <h2>Editar Envase</h2>
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <Label>Nombre del Envase</Label>
          <Input
            type="text"
            name="nombre"
            value={envase.nombre}
            onChange={handleChange}
            required
          />
          <Label>Peso en gramos</Label>
          <Input
            type="number"
            name="pesoGramos"
            value={envase.pesoGramos}
            onChange={handleChange}
            required
          />
          <div className="custom-button-row">
            <Button type="submit" className="auth-button">
              Guardar
            </Button>
            <Link to="/envases" className="auth-button">
              Cancelar
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
