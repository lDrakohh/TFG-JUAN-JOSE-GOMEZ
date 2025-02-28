import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function EmpresaEditAdmin() {
  const emptyItem = {
    id: null,
    nombreEmpresa: "",
    nombrePropietario: "",
    apellidoPropietario: "",
    direccion: "",
    cif: "",
    moneda: "EUROS",
  };

  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [empresa, setEmpresa] = useFetchState(
    emptyItem,
    `/api/v1/empresas/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );

  function handleChange(event) {
    const { name, value } = event.target;
    setEmpresa({ ...empresa, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch(`/api/v1/empresas${empresa.id ? "/" + empresa.id : ""}`, {
      method: empresa.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(empresa),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/empresas";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <h2>{empresa.id ? "Editar Empresa" : "Añadir Empresa"}</h2>
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="nombreEmpresa" className="custom-form-input-label">
              Nombre de la Empresa
            </Label>
            <Input
              type="text"
              required
              name="nombreEmpresa"
              id="nombreEmpresa"
              value={empresa.nombreEmpresa || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="nombrePropietario" className="custom-form-input-label">
              Nombre del Propietario
            </Label>
            <Input
              type="text"
              required
              name="nombrePropietario"
              id="nombrePropietario"
              value={empresa.nombrePropietario || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="apellidoPropietario" className="custom-form-input-label">
              Apellido del Propietario
            </Label>
            <Input
              type="text"
              required
              name="apellidoPropietario"
              id="apellidoPropietario"
              value={empresa.apellidoPropietario || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="direccion" className="custom-form-input-label">
              Dirección
            </Label>
            <Input
              type="text"
              required
              name="direccion"
              id="direccion"
              value={empresa.direccion || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="cif" className="custom-form-input-label">
              CIF
            </Label>
            <Input
              type="text"
              required
              name="cif"
              id="cif"
              value={empresa.cif || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="moneda" className="custom-form-input-label">
              Moneda
            </Label>
            <Input
              type="select"
              required
              name="moneda"
              id="moneda"
              value={empresa.moneda}
              onChange={handleChange}
              className="custom-input"
            >
              <option value="EUROS">Euros</option>
              <option value="LEU">Leu</option>
              <option value="ZLOTY">Zloty</option>
            </Input>
          </div>

          <div className="custom-button-row">
            <button className="auth-button">Guardar</button>
            <Link
              to={`/empresas`}
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
