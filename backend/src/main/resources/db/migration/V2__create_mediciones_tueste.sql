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