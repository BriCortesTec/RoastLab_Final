-- RoastLab: respaldo completo (migraciones V1..V7). Uso: mysql -u root -p < RoastLab_backup.sql
CREATE DATABASE IF NOT EXISTS roastlab_final CHARACTER SET utf8mb4;
USE roastlab_final;

-- ===== V1__create_schema.sql =====
-- ROASTLAB_FINAL
-- V1: Creacion del esquema inicial

-- 1. ROLES
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
) ENGINE=InnoDB;

-- 2. USUARIOS
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES roles(id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. LOTES DE CAFE
CREATE TABLE lotes_cafe (
    id_lote INT AUTO_INCREMENT PRIMARY KEY,
    codigo_lote VARCHAR(40) NOT NULL UNIQUE,
    nombre_cafe VARCHAR(100) NOT NULL,
    origen VARCHAR(120),
    variedad VARCHAR(80),
    proceso VARCHAR(60),
    fecha_cosecha DATE,
    peso_disponible_kg DECIMAL(10,2) NOT NULL DEFAULT 0,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. PERFILES DE TUESTE
CREATE TABLE perfiles_tueste (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    temperatura_inicial DECIMAL(5,2),
    temperatura_final DECIMAL(5,2),
    duracion_objetivo_min INT,
    nivel_tueste VARCHAR(30) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. TUESTES
CREATE TABLE tuestes (
    id_tueste INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_perfil INT,
    nombre VARCHAR(100) NOT NULL,
    fecha_tueste DATETIME NOT NULL,
    temperatura_maxima DECIMAL(5,2),
    duracion_min DECIMAL(6,2),
    peso_entrada_kg DECIMAL(10,2),
    peso_salida_kg DECIMAL(10,2),
    observaciones TEXT,
    estado VARCHAR(30) NOT NULL DEFAULT 'Programado',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tueste_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_tueste_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfiles_tueste(id_perfil)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- 6. RELACION TUESTE - LOTE (N:M)
CREATE TABLE tueste_lote (
    id_tueste INT NOT NULL,
    id_lote INT NOT NULL,
    cantidad_utilizada_kg DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (id_tueste, id_lote),

    CONSTRAINT fk_tueste_lote_tueste
        FOREIGN KEY (id_tueste)
        REFERENCES tuestes(id_tueste)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_tueste_lote_lote
        FOREIGN KEY (id_lote)
        REFERENCES lotes_cafe(id_lote)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ===== V2__create_mediciones_tueste.sql =====
-- ROASTLAB_FINAL
-- V2: Crear tabla de mediciones de tueste

CREATE TABLE mediciones_tueste (
    id_medicion INT AUTO_INCREMENT PRIMARY KEY,
    id_tueste INT NOT NULL,
    tiempo_segundos INT NOT NULL,
    temperatura_grano_c DECIMAL(5,2) NOT NULL,

    CONSTRAINT uq_medicion_tiempo
        UNIQUE (id_tueste, tiempo_segundos),

    CONSTRAINT fk_medicion_tueste
        FOREIGN KEY (id_tueste)
        REFERENCES tuestes(id_tueste)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;
-- ===== V3__normalize_cafes_and_lots.sql =====
-- 1. Crear el catálogo independiente de cafés
CREATE TABLE cafes (
    id_cafe INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cafe VARCHAR(100) NOT NULL,
    origen VARCHAR(120),
    variedad VARCHAR(80),
    proceso VARCHAR(60),
    notas VARCHAR(500),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Migrar los datos de café que actualmente están en los lotes
INSERT INTO cafes (nombre_cafe, origen, variedad, proceso)
SELECT DISTINCT nombre_cafe, origen, variedad, proceso
FROM lotes_cafe;

-- 3. Agregar la relación y las características físicas del lote
ALTER TABLE lotes_cafe
    ADD COLUMN id_cafe INT NULL,
    ADD COLUMN densidad DECIMAL(8,2),
    ADD COLUMN humedad DECIMAL(5,2),
    ADD COLUMN altitud INT;

-- 4. Relacionar los lotes con su café correspondiente
UPDATE lotes_cafe l
INNER JOIN cafes c
    ON c.nombre_cafe = l.nombre_cafe
    AND c.origen <=> l.origen
    AND c.variedad <=> l.variedad
    AND c.proceso <=> l.proceso
SET l.id_cafe = c.id_cafe;

-- 5. Establecer la llave foránea
ALTER TABLE lotes_cafe
    MODIFY COLUMN id_cafe INT NOT NULL,
    ADD CONSTRAINT fk_lotes_cafe_cafes
        FOREIGN KEY (id_cafe)
        REFERENCES cafes(id_cafe)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

-- 6. Eliminar los datos de café que ahora pertenecen a la tabla cafes
ALTER TABLE lotes_cafe
    DROP COLUMN nombre_cafe,
    DROP COLUMN origen,
    DROP COLUMN variedad,
    DROP COLUMN proceso;
-- ===== V4__extend_roast_profiles_and_measurements.sql =====
-- 1. Ampliar la informacion de los perfiles
ALTER TABLE perfiles_tueste
    ADD COLUMN notas_sabor VARCHAR(500),
    MODIFY COLUMN duracion_objetivo_min DECIMAL(6,2);

-- 2. Identificar eventos dentro de las mediciones reales
ALTER TABLE mediciones_tueste
    ADD COLUMN tipo_evento VARCHAR(30) NULL;

-- 3. Mediciones de las curvas objetivo de los perfiles
CREATE TABLE mediciones_perfil (
    id_medicion_perfil INT AUTO_INCREMENT PRIMARY KEY,
    id_perfil INT NOT NULL,
    tiempo_segundos INT NOT NULL,
    temperatura_objetivo_c DECIMAL(5,2) NOT NULL,
    tipo_evento VARCHAR(30),

    CONSTRAINT uq_medicion_perfil_tiempo
        UNIQUE (id_perfil, tiempo_segundos),

    CONSTRAINT fk_medicion_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfiles_tueste(id_perfil)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Ajustes realizados durante un tueste
CREATE TABLE ajustes_tueste (
    id_ajuste INT AUTO_INCREMENT PRIMARY KEY,
    id_tueste INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    etiqueta VARCHAR(100),
    valor DECIMAL(8,2) NOT NULL,
    unidad VARCHAR(20),
    tiempo_segundos INT NOT NULL,
    temperatura_grano_c DECIMAL(5,2),

    CONSTRAINT fk_ajuste_tueste
        FOREIGN KEY (id_tueste)
        REFERENCES tuestes(id_tueste)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Ajustes de referencia de los perfiles
CREATE TABLE ajustes_perfil (
    id_ajuste_perfil INT AUTO_INCREMENT PRIMARY KEY,
    id_perfil INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    etiqueta VARCHAR(100),
    valor DECIMAL(8,2) NOT NULL,
    unidad VARCHAR(20),
    tiempo_segundos INT NOT NULL,
    temperatura_objetivo_c DECIMAL(5,2),

    CONSTRAINT fk_ajuste_perfil
        FOREIGN KEY (id_perfil)
        REFERENCES perfiles_tueste(id_perfil)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;
-- ===== V5__insertar_roles.sql =====
-- V5: Insertar los roles iniciales del sistema

INSERT INTO roles (nombre, descripcion)
SELECT datos.nombre, datos.descripcion
FROM (
    SELECT 'tostador' AS nombre,
           'Registra y consulta los procesos de tueste.' AS descripcion
    UNION ALL
    SELECT 'gerencia',
           'Supervisa la operación y consulta información de tuestes.'
    UNION ALL
    SELECT 'administración',
           'Administra usuarios y configuración del sistema.'
) AS datos
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE roles.nombre = datos.nombre
);
-- ===== V6__security_notifications.sql =====
-- V6: telefono, tokens de sesion/recuperacion y bitacora de notificaciones
-- (los roles ya los inserta V5__insertar_roles.sql)

ALTER TABLE usuarios ADD COLUMN telefono VARCHAR(20) NULL;

-- Invalidacion de JWT al cerrar sesion (lista negra por jti)
CREATE TABLE tokens_revocados (
    id_token INT AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(64) NOT NULL UNIQUE,
    expira_en DATETIME NOT NULL,
    fecha_revocacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tokens_revocados_expira (expira_en)
) ENGINE=InnoDB;

-- Recuperacion de contrasena
CREATE TABLE password_reset_tokens (
    id_token INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    expira_en DATETIME NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reset_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bitacora de correos, SMS y WhatsApp enviados por el sistema
CREATE TABLE notificaciones (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL,
    canal VARCHAR(20) NOT NULL,
    destino VARCHAR(150) NOT NULL,
    asunto VARCHAR(150),
    mensaje TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    error VARCHAR(500),
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- ===== V7__seed_data.sql =====
-- V7: datos de prueba (seed). Contrasena de TODOS los usuarios: RoastLab#2026

INSERT INTO usuarios (id_usuario,id_rol,nombre,apellido,correo,password_hash,activo) VALUES
(1,(SELECT id_rol FROM roles WHERE nombre='administración'),'Administrador','RoastLab','admin@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(2,(SELECT id_rol FROM roles WHERE nombre='gerencia'),'Gabriela','Mendoza','gerente@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(3,(SELECT id_rol FROM roles WHERE nombre='gerencia'),'Ricardo','Salinas','gerente2@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(4,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Tomas','Aguilar','tostador@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(5,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Valeria','Cruz','valeria.cruz@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(6,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Mateo','Ramirez','mateo.ramirez@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(7,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Camila','Lopez','camila.lopez@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(8,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Diego','Herrera','diego.herrera@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(9,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Sofia','Vargas','sofia.vargas@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(10,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Andres','Castillo','andres.castillo@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(11,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Lucia','Ortega','lucia.ortega@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1),
(12,(SELECT id_rol FROM roles WHERE nombre='tostador'),'Javier','Morales','javier.morales@roastlab.site','$2b$10$rJ8yGXxQzWNkqd7bwRD3TOOhlVsStE7ys00Xf8h/jRtrhY7mrBrCa',1);

INSERT INTO cafes (id_cafe,nombre_cafe,origen,variedad,proceso,notas) VALUES
(1,'Finca La Esperanza','Chiapas, Mexico','Caturra','Lavado','Chocolate, nuez y acidez media'),
(2,'El Mirador','Veracruz, Mexico','Caturra','Natural','Frutos rojos y chocolate'),
(3,'Las Nubes','Oaxaca, Mexico','Bourbon','Honey','Miel y caramelo'),
(4,'Pluma Hidalgo','Oaxaca, Mexico','Typica','Lavado','Floral, citrico suave'),
(5,'Sierra Madre','Chiapas, Mexico','Garnica','Lavado','Panela y almendra'),
(6,'Altos de Huehuetenango','Huehuetenango, Guatemala','Bourbon','Lavado','Cacao y naranja'),
(7,'Volcan Poas','Alajuela, Costa Rica','Catuai','Honey','Durazno y miel'),
(8,'Yirgacheffe Kochere','Yirgacheffe, Etiopia','Heirloom','Natural','Arandano y jazmin'),
(9,'Huila Reserva','Huila, Colombia','Castillo','Lavado','Caramelo y manzana verde'),
(10,'Cerrado Dulce','Minas Gerais, Brasil','Mundo Novo','Natural','Cacahuate y chocolate con leche'),
(11,'Tarrazu Selecto','Tarrazu, Costa Rica','Caturra','Lavado','Brillante, limon y panela'),
(12,'Nariño Alto','Narino, Colombia','Caturra','Honey','Ciruela y vainilla');

INSERT INTO lotes_cafe (id_lote,codigo_lote,id_cafe,densidad,humedad,altitud,fecha_cosecha,peso_disponible_kg) VALUES
(1,'CH-24-107',1,725.34,9.88,1950,'2026-08-03',51.96),
(2,'VE-24-114',2,693.18,10.96,1800,'2026-08-02',18.87),
(3,'OA-24-121',3,738.54,10.1,1800,'2026-09-02',52.21),
(4,'OA-24-128',4,697.33,10.06,1950,'2026-10-02',40.97),
(5,'CH-24-135',5,735.54,11.94,1200,'2026-10-28',20.99),
(6,'HU-24-142',6,738.68,10.85,1800,'2026-09-18',51.73),
(7,'AL-24-149',7,705.3,10.95,1950,'2026-08-12',19.38),
(8,'YI-24-156',8,779.7,10.91,1800,'2026-08-16',45.62),
(9,'HU-24-163',9,739.86,10.29,1800,'2026-09-12',28.49),
(10,'MI-24-170',10,791.21,11.25,1350,'2026-08-19',28.51),
(11,'TA-24-177',11,749.32,10.36,1650,'2026-09-20',59.11),
(12,'NA-24-184',12,696.53,10.55,1500,'2026-08-16',33.98);

INSERT INTO perfiles_tueste (id_perfil,nombre,descripcion,temperatura_inicial,temperatura_final,duracion_objetivo_min,nivel_tueste,notas_sabor) VALUES
(1,'Claro para filtro','Perfil de referencia: Claro para filtro',178,198,9.00,'Claro','Notas esperadas del perfil claro para filtro'),
(2,'Balanceado de la casa','Perfil de referencia: Balanceado de la casa',185,205,11.00,'Medio','Notas esperadas del perfil balanceado de la casa'),
(3,'Espresso intenso','Perfil de referencia: Espresso intenso',190,214,13.00,'Oscuro','Notas esperadas del perfil espresso intenso'),
(4,'Ligero floral','Perfil de referencia: Ligero floral',178,198,9.00,'Claro','Notas esperadas del perfil ligero floral'),
(5,'Medio dulce','Perfil de referencia: Medio dulce',185,205,11.00,'Medio','Notas esperadas del perfil medio dulce'),
(6,'Oscuro italiano','Perfil de referencia: Oscuro italiano',190,214,13.00,'Oscuro','Notas esperadas del perfil oscuro italiano'),
(7,'Cold brew','Perfil de referencia: Cold brew',190,214,13.00,'Oscuro','Notas esperadas del perfil cold brew'),
(8,'Single origin suave','Perfil de referencia: Single origin suave',178,198,9.00,'Claro','Notas esperadas del perfil single origin suave'),
(9,'Mezcla desayuno','Perfil de referencia: Mezcla desayuno',185,205,11.00,'Medio','Notas esperadas del perfil mezcla desayuno'),
(10,'Prensa francesa','Perfil de referencia: Prensa francesa',185,205,11.00,'Medio','Notas esperadas del perfil prensa francesa'),
(11,'Reserva de la casa','Perfil de referencia: Reserva de la casa',185,205,11.00,'Medio','Notas esperadas del perfil reserva de la casa'),
(12,'Descafeinado medio','Perfil de referencia: Descafeinado medio',185,205,11.00,'Medio','Notas esperadas del perfil descafeinado medio');

INSERT INTO mediciones_perfil (id_perfil,tiempo_segundos,temperatura_objetivo_c,tipo_evento) VALUES
(1,0,178,'charge'),
(1,85,130,'tp'),
(1,164,143.17,NULL),
(1,243,156,'dry_end'),
(1,332,177.87,NULL),
(1,421,198,'fc_start'),
(1,540,198,'drop'),
(2,0,185,'charge'),
(2,74,140,'tp'),
(2,148,147.87,NULL),
(2,222,157.02,NULL),
(2,297,164,'dry_end'),
(2,369,176.67,NULL),
(2,441,186.59,NULL),
(2,514,198,'fc_start'),
(2,587,201.99,NULL),
(2,660,205,'drop'),
(3,0,190,'charge'),
(3,75,143,'tp'),
(3,144,147.85,NULL),
(3,213,153.66,NULL),
(3,282,160.01,NULL),
(3,351,165,'dry_end'),
(3,415,171.82,NULL),
(3,479,181.39,NULL),
(3,543,188.75,NULL),
(3,608,198,'fc_start'),
(3,717,225,'sc_start'),
(3,780,214,'drop'),
(4,0,178,'charge'),
(4,79,136,'tp'),
(4,161,145.74,NULL),
(4,243,157,'dry_end'),
(4,332,176.67,NULL),
(4,421,197,'fc_start'),
(4,540,198,'drop'),
(5,0,185,'charge'),
(5,67,139,'tp'),
(5,143,146.62,NULL),
(5,220,153.43,NULL),
(5,297,159,'dry_end'),
(5,369,169.67,NULL),
(5,441,181.41,NULL),
(5,514,193,'fc_start'),
(5,587,198.58,NULL),
(5,660,205,'drop'),
(6,0,190,'charge'),
(6,60,150,'tp'),
(6,132,150.54,NULL),
(6,205,154.99,NULL),
(6,278,155.05,NULL),
(6,351,158,'dry_end'),
(6,415,167.1,NULL),
(6,479,176.44,NULL),
(6,543,187.35,NULL),
(6,608,197,'fc_start'),
(6,717,223,'sc_start'),
(6,780,214,'drop'),
(7,0,190,'charge'),
(7,71,143,'tp'),
(7,141,147.16,NULL),
(7,211,155.2,NULL),
(7,281,160.34,NULL),
(7,351,165,'dry_end'),
(7,415,174.12,NULL),
(7,479,181.89,NULL),
(7,543,188.68,NULL),
(7,608,197,'fc_start'),
(7,717,227,'sc_start'),
(7,780,214,'drop'),
(8,0,178,'charge'),
(8,75,134,'tp'),
(8,159,143.2,NULL),
(8,243,155,'dry_end'),
(8,332,173.13,NULL),
(8,421,193,'fc_start'),
(8,540,198,'drop'),
(9,0,185,'charge'),
(9,56,146,'tp'),
(9,116,147.2,NULL),
(9,176,149.3,NULL),
(9,236,152.34,NULL),
(9,297,155,'dry_end'),
(9,369,167.24,NULL),
(9,441,183.46,NULL),
(9,514,196,'fc_start'),
(9,587,200.84,NULL),
(9,660,205,'drop'),
(10,0,185,'charge'),
(10,74,142,'tp'),
(10,148,147.51,NULL),
(10,222,155.3,NULL),
(10,297,162,'dry_end'),
(10,369,173.43,NULL),
(10,441,181.94,NULL),
(10,514,192,'fc_start'),
(10,587,197.94,NULL),
(10,660,205,'drop'),
(11,0,185,'charge'),
(11,78,143,'tp'),
(11,151,149.91,NULL),
(11,224,155.72,NULL),
(11,297,162,'dry_end'),
(11,369,173.12,NULL),
(11,441,187.36,NULL),
(11,514,198,'fc_start'),
(11,587,201.09,NULL),
(11,660,205,'drop'),
(12,0,185,'charge'),
(12,75,146,'tp'),
(12,149,149.93,NULL),
(12,223,153.67,NULL),
(12,297,159,'dry_end'),
(12,369,172.15,NULL),
(12,441,183.76,NULL),
(12,514,196,'fc_start'),
(12,587,201.34,NULL),
(12,660,205,'drop');

INSERT INTO ajustes_perfil (id_perfil,tipo,etiqueta,valor,unidad,tiempo_segundos,temperatura_objetivo_c) VALUES
(1,'heat','Calor / quemador',70,'%',143,NULL),
(1,'airflow','Flujo de aire',4,'nivel',344,NULL),
(2,'heat','Calor / quemador',60,'%',193,NULL),
(2,'airflow','Flujo de aire',4,'nivel',339,NULL),
(3,'heat','Calor / quemador',60,'%',163,NULL),
(3,'airflow','Flujo de aire',2,'nivel',327,NULL),
(4,'heat','Calor / quemador',75,'%',110,NULL),
(4,'airflow','Flujo de aire',2,'nivel',357,NULL),
(5,'heat','Calor / quemador',75,'%',129,NULL),
(5,'airflow','Flujo de aire',2,'nivel',310,NULL),
(6,'heat','Calor / quemador',70,'%',116,NULL),
(6,'airflow','Flujo de aire',4,'nivel',409,NULL),
(7,'heat','Calor / quemador',75,'%',150,NULL),
(7,'airflow','Flujo de aire',2,'nivel',361,NULL),
(8,'heat','Calor / quemador',65,'%',114,NULL),
(8,'airflow','Flujo de aire',3,'nivel',376,NULL),
(9,'heat','Calor / quemador',65,'%',181,NULL),
(9,'airflow','Flujo de aire',3,'nivel',344,NULL),
(10,'heat','Calor / quemador',65,'%',113,NULL),
(10,'airflow','Flujo de aire',4,'nivel',343,NULL),
(11,'heat','Calor / quemador',60,'%',197,NULL),
(11,'airflow','Flujo de aire',4,'nivel',338,NULL),
(12,'heat','Calor / quemador',70,'%',181,NULL),
(12,'airflow','Flujo de aire',2,'nivel',378,NULL);

INSERT INTO tuestes (id_tueste,id_usuario,id_perfil,nombre,fecha_tueste,temperatura_maxima,duracion_min,peso_entrada_kg,peso_salida_kg,observaciones,estado) VALUES
(1,4,1,'Tueste #021','2026-11-02 09:30:00',201,10.0,11.88,10.35,'Tueste de prueba registrado en seed','Completado'),
(2,4,2,'Tueste #022','2026-11-03 10:30:00',207,11.62,11.65,9.87,'Tueste de prueba registrado en seed','Completado'),
(3,5,3,'Tueste #023','2026-11-04 11:30:00',223,13.07,10.4,8.58,'Tueste de prueba registrado en seed','Completado'),
(4,6,4,'Tueste #024','2026-11-05 12:30:00',201,8.38,5.21,4.56,'Tueste de prueba registrado en seed','Completado'),
(5,7,5,'Tueste #025','2026-11-06 13:30:00',207,11.5,5.05,4.15,'Tueste de prueba registrado en seed','Completado'),
(6,8,6,'Tueste #026','2026-11-07 14:30:00',227,13.4,5.92,5.06,'Tueste de prueba registrado en seed','Completado'),
(7,9,7,'Tueste #027','2026-11-08 15:30:00',225,12.37,10.98,9.52,'Tueste de prueba registrado en seed','Completado'),
(8,10,8,'Tueste #028','2026-11-09 16:30:00',195,8.85,5.53,4.55,'Tueste de prueba registrado en seed','Completado'),
(9,11,9,'Tueste #029','2026-11-10 08:30:00',204,11.4,8.85,7.36,'Tueste de prueba registrado en seed','Completado'),
(10,12,10,'Tueste #030','2026-11-11 09:30:00',207,10.58,7.14,5.99,'Tueste de prueba registrado en seed','Completado'),
(11,5,11,'Tueste #031','2026-11-12 10:30:00',203,10.87,11.06,9.71,'Tueste de prueba registrado en seed','Completado'),
(12,6,12,'Tueste #032','2026-11-13 11:30:00',204,10.75,6.85,5.65,'Tueste de prueba registrado en seed','Completado');

INSERT INTO tueste_lote (id_tueste,id_lote,cantidad_utilizada_kg) VALUES
(1,1,11.88),
(2,2,11.65),
(3,3,10.4),
(4,4,2.6),
(4,5,2.61),
(5,5,5.05),
(6,6,5.92),
(7,7,10.98),
(8,8,2.77),
(8,9,2.76),
(9,9,8.85),
(10,10,7.14),
(11,11,11.06),
(12,12,3.42),
(12,1,3.43);

INSERT INTO mediciones_tueste (id_tueste,tiempo_segundos,temperatura_grano_c,tipo_evento) VALUES
(1,0,178,'charge'),
(1,62,134,'tp'),
(1,131,142.05,NULL),
(1,200,149.57,NULL),
(1,270,158,'dry_end'),
(1,336,168.25,NULL),
(1,402,179.92,NULL),
(1,468,193,'fc_start'),
(1,534,196.34,NULL),
(1,600,201,'drop'),
(2,0,185,'charge'),
(2,66,142,'tp'),
(2,127,144.31,NULL),
(2,189,148.91,NULL),
(2,251,152.01,NULL),
(2,313,156,'dry_end'),
(2,389,168.28,NULL),
(2,466,182.12,NULL),
(2,543,193,'fc_start'),
(2,620,200.33,NULL),
(2,697,207,'drop'),
(3,0,190,'charge'),
(3,58,146,'tp'),
(3,131,148.8,NULL),
(3,205,152.41,NULL),
(3,278,153.76,NULL),
(3,352,158,'dry_end'),
(3,416,168.59,NULL),
(3,481,177.17,NULL),
(3,546,185.64,NULL),
(3,611,195,'fc_start'),
(3,721,223,'sc_start'),
(3,784,216,'drop'),
(4,0,178,'charge'),
(4,75,138,'tp'),
(4,150,151.29,NULL),
(4,226,164,'dry_end'),
(4,309,180.92,NULL),
(4,392,198,'fc_start'),
(4,503,201,'drop'),
(5,0,185,'charge'),
(5,75,146,'tp'),
(5,153,152.97,NULL),
(5,231,157.13,NULL),
(5,310,163,'dry_end'),
(5,386,175.45,NULL),
(5,462,186.65,NULL),
(5,538,197,'fc_start'),
(5,614,201.13,NULL),
(5,690,207,'drop'),
(6,0,190,'charge'),
(6,72,146,'tp'),
(6,144,148.31,NULL),
(6,216,151.37,NULL),
(6,288,154.5,NULL),
(6,361,157,'dry_end'),
(6,427,166.96,NULL),
(6,494,174.26,NULL),
(6,560,184.5,NULL),
(6,627,192,'fc_start'),
(6,739,227,'sc_start'),
(6,804,213,'drop'),
(7,0,190,'charge'),
(7,55,150,'tp'),
(7,124,152.11,NULL),
(7,194,152.36,NULL),
(7,263,153.94,NULL),
(7,333,157,'dry_end'),
(7,394,166.55,NULL),
(7,455,175.09,NULL),
(7,516,183.95,NULL),
(7,578,193,'fc_start'),
(7,682,225,'sc_start'),
(7,742,215,'drop'),
(8,0,178,'charge'),
(8,71,133,'tp'),
(8,154,148.78,NULL),
(8,238,163,'dry_end'),
(8,326,178.74,NULL),
(8,414,192,'fc_start'),
(8,531,195,'drop'),
(9,0,185,'charge'),
(9,69,139,'tp'),
(9,148,146.93,NULL),
(9,227,156.32,NULL),
(9,307,163,'dry_end'),
(9,382,175.26,NULL),
(9,457,187.46,NULL),
(9,533,198,'fc_start'),
(9,608,202.33,NULL),
(9,684,204,'drop'),
(10,0,185,'charge'),
(10,62,141,'tp'),
(10,136,146.51,NULL),
(10,210,151.85,NULL),
(10,285,156,'dry_end'),
(10,355,169.52,NULL),
(10,425,179.63,NULL),
(10,495,193,'fc_start'),
(10,565,200.65,NULL),
(10,635,207,'drop'),
(11,0,185,'charge'),
(11,78,146,'tp'),
(11,149,149.99,NULL),
(11,221,156.5,NULL),
(11,293,161,'dry_end'),
(11,364,171.5,NULL),
(11,436,184.29,NULL),
(11,508,195,'fc_start'),
(11,580,200.48,NULL),
(11,652,203,'drop'),
(12,0,185,'charge'),
(12,55,142,'tp'),
(12,133,148.82,NULL),
(12,211,154.55,NULL),
(12,290,163,'dry_end'),
(12,361,173.16,NULL),
(12,432,184.71,NULL),
(12,503,195,'fc_start'),
(12,574,199.54,NULL),
(12,645,204,'drop');

INSERT INTO ajustes_tueste (id_tueste,tipo,etiqueta,valor,unidad,tiempo_segundos,temperatura_grano_c) VALUES
(1,'heat','Calor / quemador',65,'%',114,172),
(2,'heat','Calor / quemador',60,'%',151,170),
(3,'heat','Calor / quemador',70,'%',100,173),
(3,'drum','Velocidad del tambor',52,'rpm',221,164),
(4,'heat','Calor / quemador',65,'%',109,167),
(5,'heat','Calor / quemador',65,'%',117,159),
(6,'heat','Calor / quemador',70,'%',106,167),
(6,'drum','Velocidad del tambor',52,'rpm',267,176),
(7,'heat','Calor / quemador',60,'%',161,151),
(8,'heat','Calor / quemador',65,'%',131,169),
(9,'heat','Calor / quemador',65,'%',161,156),
(9,'drum','Velocidad del tambor',55,'rpm',217,173),
(10,'heat','Calor / quemador',70,'%',136,154),
(11,'heat','Calor / quemador',65,'%',133,163),
(12,'heat','Calor / quemador',60,'%',104,175),
(12,'drum','Velocidad del tambor',52,'rpm',213,162);
