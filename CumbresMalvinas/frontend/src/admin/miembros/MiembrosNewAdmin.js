import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Form, Input, Label, Button } from 'reactstrap';
import tokenService from '../../services/token.service';
import getErrorModal from '../../util/getErrorModal';

const MiembroNewAdmin = () => {
  const { id } = useParams(); // Obtén el ID de la organización desde la URL
  const [miembro, setMiembro] = useState({
    nombre: '',
    telefono: '',
    propiedades: '',
    comentarios: '',
    organizacion_id: id // Asignar el ID de la organización directamente
  });
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setMiembro({ ...miembro, [name]: value });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    fetch(`/api/v1/miembros`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${tokenService.getLocalAccessToken()}`
      },
      body: JSON.stringify(miembro)
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Error al crear el miembro');
        }
        return response.json();
      })
      .then(() => {
        // Redireccionar a la lista de miembros después de crear uno nuevo
        window.location.href = `/organizaciones/${id}/miembros`;
      })
      .catch((error) => {
        console.error("Error:", error);
        setMessage(error.message);
        setVisible(true);
      });
  };

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div>
      {modal}
      <h2>Añadir Nuevo Miembro</h2>
      <Form onSubmit={handleSubmit}>
        <div>
          <Label for="nombre">Nombre</Label>
          <Input
            type="text"
            name="nombre"
            id="nombre"
            value={miembro.nombre}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <Label for="telefono">Teléfono</Label>
          <Input
            type="text"
            name="telefono"
            id="telefono"
            value={miembro.telefono}
            onChange={handleChange}
          />
        </div>
        <div>
          <Label for="propiedades">Propiedades</Label>
          <Input
            type="text"
            name="propiedades"
            id="propiedades"
            value={miembro.propiedades}
            onChange={handleChange}
          />
        </div>
        <div>
          <Label for="comentarios">Comentarios</Label>
          <Input
            type="textarea"
            name="comentarios"
            id="comentarios"
            value={miembro.comentarios}
            onChange={handleChange}
          />
        </div>
        <Button type="submit">Guardar</Button>
        <Link to={`/organizaciones/${id}/miembros`} className="btn btn-secondary">Cancelar</Link>
      </Form>
    </div>
  );
};

export default MiembroNewAdmin;
