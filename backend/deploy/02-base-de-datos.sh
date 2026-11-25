#!/bin/bash
# Ejecutar en el VPS como root:  DB_PASSWORD='una-clave-fuerte' bash 02-base-de-datos.sh
# Crea la base y un usuario propio (NO se usa root desde la aplicacion).
set -euo pipefail
: "${DB_PASSWORD:?Define DB_PASSWORD, por ejemplo: DB_PASSWORD='...' bash 02-base-de-datos.sh}"

mysql <<SQL
CREATE DATABASE IF NOT EXISTS roastlab_final CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'roastlab'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
ALTER USER 'roastlab'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON roastlab_final.* TO 'roastlab'@'localhost';
FLUSH PRIVILEGES;
SQL
echo "Base roastlab_final lista. Flyway creara las tablas y los datos de prueba al arrancar el backend."
