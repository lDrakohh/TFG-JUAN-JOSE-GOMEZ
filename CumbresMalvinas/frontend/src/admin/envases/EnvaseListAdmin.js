import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table, Input } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function EnvaseListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [envases, setEnvases] = useFetchState(
    [],
    `/api/v1/envases`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  // Mapeo de envases para renderizar la tabla
  const envaseList = envases.map((envase) => {
    return (
      <tr key={envase.id}>
        <td>{envase.nombre}</td>
        <td>{envase.pesoGramos} gramos</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + envase.id}
              tag={Link}
              to={"/envases/" + envase.id}
            >
              Editar
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + envase.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/envases/${envase.id}`,
                  envase.id,
                  [envases, setEnvases],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Eliminar
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Envases</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/envases/new">
        Añadir Envase
      </Button>
      <div>
        <Table aria-label="envases" className="mt-4">
          <thead>
            <tr>
              <th>Nombre Envase</th>
              <th>Peso en gramos</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>{envaseList}</tbody>
        </Table>
      </div>
    </div>
  );
}
