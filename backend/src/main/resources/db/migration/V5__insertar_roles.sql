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