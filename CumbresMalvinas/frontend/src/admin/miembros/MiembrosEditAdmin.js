import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";

const jwt = tokenService.getLocalAccessToken();

export default function MiembroEditAdmin() {
  const emptyItem = {
    id: null,
    nombre: "",
    telefono: "",
    propiedades: "",
    comentarios: "",
  };

  const id = getIdFromUrl(2); // Asegúrate de que esta función esté obteniendo el ID correcto
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [miembro, setMiembro] = useState(emptyItem);

  useEffect(() => {
    // Obtener los detalles del miembro al cargar el componente
    fetch(`/api/v1/miembros/${id}`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Error al obtener el miembro");
        }
        return response.json();
      })
      .then((data) => {
        setMiembro(data); // Ajusta esto dependiendo de la estructura de tu respuesta
      })
      .catch((error) => {
        setMessage(error.message);
        setVisible(true);
      });
  }, [id]); // Dependencia del ID del miembro

  function handleChange(event) {
    const { name, value } = event.target;
    setMiembro({ ...miembro, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch(`/api/v1/miembros/${miembro.id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(miembro),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else {
          window.location.href = "/organizaciones";
        }
      })
      .catch((error) => alert(error.message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <h2>{miembro.id ? "Editar Miembro" : "Agregar Miembro"}</h2>
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="nombre" className="custom-form-input-label">
              Nombre del Miembro
            </Label>
            <Input
              type="text"
              required
              name="nombre"
              id="nombre"
              value={miembro.nombre || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="telefono" className="custom-form-input-label">
              Teléfono
            </Label>
            <Input
              type="text"
              required
              name="telefono"
              id="telefono"
              value={miembro.telefono || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="propiedades" className="custom-form-input-label">
              Propiedades
            </Label>
            <Input
              type="text"
              required
              name="propiedades"
              id="propiedades"
              value={miembro.propiedades || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="comentarios" className="custom-form-input-label">
              Comentarios
            </Label>
            <Input
              type="textarea"
              required
              name="comentarios"
              id="comentarios"
              value={miembro.comentarios || ""}
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
