import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function OrganizacionEditAdmin() {
  const emptyItem = {
    id: null,
    nombre: "",
    descripcion: "",
    miembros: [],
  };

  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [organizacion, setOrganizacion] = useFetchState(
    emptyItem,
    `/api/v1/organizaciones/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    setOrganizacion({ ...organizacion, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch("/api/v1/organizaciones" + (organizacion.id ? "/" + organizacion.id : ""), {
      method: organizacion.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(organizacion),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/organizaciones";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <h2>{organizacion.id ? "Edit Organization" : "Add Organization"}</h2>
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="nombre" className="custom-form-input-label">
              Nombre de la organización
            </Label>
            <Input
              type="text"
              required
              name="nombre"
              id="nombre"
              value={organizacion.nombre || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="descripcion" className="custom-form-input-label">
              Descripción
            </Label>
            <Input
              type="textarea"
              required
              name="descripcion"
              id="descripcion"
              value={organizacion.descripcion || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>

          <div className="custom-button-row">
            <button className="auth-button">Guardar</button>
            <Link
              to={`/organizaciones`}
              className="auth-button"
              style={{ textDecoration: "none" }}
            >
              Cancelar
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
