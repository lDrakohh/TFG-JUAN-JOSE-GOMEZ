-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO authorities(id,authority) VALUES (2,'USER');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);
INSERT INTO appusers(id,username,password,authority) VALUES (2,'user1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);


INSERT INTO empresas(id,nombre_empresa,nombre_propietario,apellido_propietario,direccion,cif,moneda) VALUES (1,'Pepe SL','Pepe','Marin','Calle Buenavista','343434D','EUROS');
INSERT INTO empresas(id,nombre_empresa,nombre_propietario,apellido_propietario,direccion,cif,moneda) VALUES (2,'FresGonzalez SL','Antonio','Gonzalez','Calle Barco del Rey','1111F','ZLOTY');

INSERT INTO envases(id, nombre, peso_gramos) VALUES (1, 'EXP - 5KGs', 5000);
INSERT INTO envases(id, nombre, peso_gramos) VALUES (2, 'EXP - 4KGs', 4000);
INSERT INTO envases(id, nombre, peso_gramos) VALUES (3, 'EXP - 3KGs', 3000);

INSERT INTO frutas(id, variedad, calidad, marca, envase_id) VALUES (1, 'Fresa', 'Primera', 'Fortuna', 1);
INSERT INTO frutas(id, variedad, calidad, marca, envase_id) VALUES (2, 'Fresa', 'Segunda', '101', 2);

INSERT INTO previsiones(id, empresa_id, fruta_id, previsto, prev_traidas, prev_faltantes, fecha) VALUES (1, 1, 1, 100, 50, 50, '2025-02-28');

INSERT INTO registros_mercancia(id, prevision_id, cantidad_traida,fecha) VALUES (1, 1, 50, '2025-02-28');