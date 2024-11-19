import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Button, ButtonGroup, Table } from 'reactstrap';
import deleteFromList from '../../util/deleteFromList'; 
import getErrorModal from '../../util/getErrorModal'; 

const MiembrosListAdmin = () => {
  const { id } = useParams(); // Obtener el ID de la organización desde la URL
  const [miembros, setMiembros] = useState([]);
  const [organizacionNombre, setOrganizacionNombre] = useState(""); 
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]); 

  useEffect(() => {
    // Fetch de miembros y nombre de la organización
    fetch(`/api/v1/organizaciones/${id}/miembros`)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then((data) => {
        setMiembros(data.miembros); // Actualiza el estado con los miembros
        setOrganizacionNombre(data.organizacionNombre); 
      })
      .catch((error) => {
        console.error("Error fetching miembros:", error);
        setMessage(error.message); // Establece un mensaje de error
        setVisible(true); // Muestra el modal de error
      });
  }, [id]); // Dependencia del efecto es el ID

  const modal = getErrorModal(setVisible, visible, message); // Modal de error, si lo necesitas

  return (
    <div>
      {modal}
      <h1>Miembros de {organizacionNombre}</h1>
      
      {/* Botón para añadir un nuevo miembro */}
      <Button color="success" tag={Link} to={`/organizaciones/${id}/miembros/new`}>
        Añadir miembro
      </Button>
      
      <Table>
        <thead>
          <tr>
            <th>Descripción</th>
            <th>Nombre</th>
            <th>Teléfono</th>
            <th>Propiedades</th>
            <th>Comentarios</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {miembros.map((miembro) => (
            <tr key={miembro.id}>
              <td>Miembro de {organizacionNombre}</td>
              <td>{miembro.nombre}</td>
              <td>{miembro.telefono}</td>
              <td>{miembro.propiedades}</td>
              <td>{miembro.comentarios}</td>
              <td>
                <ButtonGroup>
                  <Button size="sm" color="primary" tag={Link} to={`/miembros/${miembro.id}/edit`}>
                    Editar
                  </Button>
                  <Button
                    size="sm"
                    color="danger"
                    aria-label={"delete-" + miembro.id}
                    onClick={() =>
                      deleteFromList(
                        `/api/v1/miembros/${miembro.id}`,
                        miembro.id,
                        [miembros, setMiembros],
                        [alerts, setAlerts],
                        setMessage,
                        setVisible
                      )
                    }
                  >
                    Borrar
                  </Button>
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
};

export default MiembrosListAdmin;
