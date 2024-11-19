import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function OrganizacionListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [organizaciones, setOrganizaciones] = useFetchState(
    [],
    `/api/v1/organizaciones`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  // Mapeo de organizaciones para renderizar la tabla
  const organizacionList = organizaciones.map((organizacion) => {
    return (
      <tr key={organizacion.id}>
        <td>{organizacion.nombre}</td>
        <td>{organizacion.descripcion}</td>
        <td>
          <Button
            size="sm"
            color="info"
            aria-label={"view-members-" + organizacion.id}
            tag={Link}
            to={`/organizaciones/${organizacion.id}/miembros`}
          >
            Ver Miembros
          </Button>
        </td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + organizacion.id}
              tag={Link}
              to={"/organizaciones/" + organizacion.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + organizacion.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/organizaciones/${organizacion.id}`,
                  organizacion.id,
                  [organizaciones, setOrganizaciones],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Organizaciones</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/organizaciones/new">
        Añadir organizacion
      </Button>
      <div>
        <Table aria-label="organizaciones" className="mt-4">
          <thead>
            <tr>
              <th>Organización</th>
              <th>Descripción</th>
              <th>Miembros</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{organizacionList}</tbody>
        </Table>
      </div>
    </div>
  );
}
