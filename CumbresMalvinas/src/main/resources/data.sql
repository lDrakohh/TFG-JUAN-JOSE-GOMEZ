-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

--organizacion y miembro para pruebas
INSERT INTO organizaciones(id,nombre,descripcion) VALUES (1,'Los hombres de Pako','HAMPA dura');
INSERT INTO organizaciones(id,nombre,descripcion) VALUES (2,'MOTORGANG','Pium pium');

INSERT INTO miembros(id, nombre, telefono, propiedades, comentarios, organizacion_id) VALUES (1, 'Juaniko_Banana','444555', '334', null,1);
INSERT INTO miembros(id, nombre, telefono, propiedades, comentarios, organizacion_id) VALUES (2, 'PAKITO','333444', '2', null,1);
INSERT INTO miembros(id, nombre, telefono, propiedades, comentarios, organizacion_id) VALUES (3, 'Kenshin','132224', '24', null,2);