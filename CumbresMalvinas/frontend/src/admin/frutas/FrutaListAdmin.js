import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table, Input } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function FrutaListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [frutas, setFrutas] = useFetchState(
    [],
    `/api/v1/frutas`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  // Mapeo de frutas para renderizar la tabla
  const frutaList = frutas.map((fruta) => {
    return (
      <tr key={fruta.id}>
        <td>{fruta.variedad}</td>
        <td>{fruta.calidad}</td>
        <td>{fruta.marca}</td>
        <td>{fruta.envase ? fruta.envase.nombre : "No asignado"}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + fruta.id}
              tag={Link}
              to={"/frutas/" + fruta.id}
            >
              Editar
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + fruta.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/frutas/${fruta.id}`,
                  fruta.id,
                  [frutas, setFrutas],
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
      <h1 className="text-center">Frutas</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/frutas/new">
        Añadir Fruta
      </Button>
      <div>
        <Table aria-label="frutas" className="mt-4">
          <thead>
            <tr>
              <th>Variedad</th>
              <th>Calidad</th>
              <th>Marca</th>
              <th>Envase</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>{frutaList}</tbody>
        </Table>
      </div>
    </div>
  );
}
