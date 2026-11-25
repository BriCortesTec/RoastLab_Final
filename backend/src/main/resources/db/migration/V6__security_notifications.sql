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
