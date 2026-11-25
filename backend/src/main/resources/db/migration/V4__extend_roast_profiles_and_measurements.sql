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