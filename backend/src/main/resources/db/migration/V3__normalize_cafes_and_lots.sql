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