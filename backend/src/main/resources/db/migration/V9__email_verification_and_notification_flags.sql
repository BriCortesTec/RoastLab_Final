-- Los usuarios existentes conservan el acceso; las cuentas nuevas deben verificar el correo.
ALTER TABLE usuarios
    ADD COLUMN correo_verificado BOOLEAN NOT NULL DEFAULT TRUE;

-- Normaliza registros anteriores sin cambiar teléfonos que no sean mexicanos válidos.
UPDATE usuarios
SET telefono = CASE
    WHEN CHAR_LENGTH(REGEXP_REPLACE(telefono, '[^0-9]', '')) = 10
        THEN CONCAT('52', REGEXP_REPLACE(telefono, '[^0-9]', ''))
    WHEN CHAR_LENGTH(REGEXP_REPLACE(telefono, '[^0-9]', '')) = 12
         AND REGEXP_REPLACE(telefono, '[^0-9]', '') LIKE '52%'
        THEN REGEXP_REPLACE(telefono, '[^0-9]', '')
    WHEN CHAR_LENGTH(REGEXP_REPLACE(telefono, '[^0-9]', '')) = 13
         AND REGEXP_REPLACE(telefono, '[^0-9]', '') LIKE '521%'
        THEN CONCAT('52', SUBSTRING(REGEXP_REPLACE(telefono, '[^0-9]', ''), 4))
    ELSE telefono
END
WHERE telefono IS NOT NULL AND TRIM(telefono) <> '';

CREATE TABLE email_verification_tokens (
    id_token INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expira_en DATETIME NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_verification_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_email_verification_usuario (id_usuario)
) ENGINE=InnoDB;
