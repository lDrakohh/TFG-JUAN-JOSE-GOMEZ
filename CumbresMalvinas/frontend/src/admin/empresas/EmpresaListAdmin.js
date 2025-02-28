import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function EmpresaListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [empresas, setEmpresas] = useFetchState(
    [],
    `/api/v1/empresas`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  // Mapeo de empresas para renderizar la tabla
  const empresaList = empresas.map((empresa) => {
    return (
      <tr key={empresa.id}>
        <td>{empresa.nombreEmpresa}</td>
        <td>{empresa.nombrePropietario} {empresa.apellidoPropietario}</td>
        <td>{empresa.direccion}</td>
        <td>{empresa.cif}</td>
        <td>{empresa.moneda}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + empresa.id}
              tag={Link}
              to={"/empresas/" + empresa.id}
            >
              Editar
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + empresa.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/empresas/${empresa.id}`,
                  empresa.id,
                  [empresas, setEmpresas],
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
      <h1 className="text-center">Empresas</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/empresas/new">
        Añadir Empresa
      </Button>
      <div>
        <Table aria-label="empresas" className="mt-4">
          <thead>
            <tr>
              <th>Nombre Empresa</th>
              <th>Propietario</th>
              <th>Dirección</th>
              <th>CIF</th>
              <th>Moneda</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>{empresaList}</tbody>
        </Table>
      </div>
    </div>
  );
}
