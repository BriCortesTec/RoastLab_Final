-- 
-- ROASTLAB_FINAL - Modelo de base de datos MySQL
-- lotes_cafeperfiles_tuesteid_perfil

CREATE DATABASE IF NOT EXISTS roastlab_final
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE roastlab_final;

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

SHOW tables;

