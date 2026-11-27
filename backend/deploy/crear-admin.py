#!/usr/bin/env python3
"""Crear el primer administrador desde la consola del VPS después de Flyway."""
from getpass import getpass
import bcrypt
import re
import subprocess

def sql_text(text):
    return "CONVERT(0x" + text.encode().hex() + " USING utf8mb4)"

def main():
    email = input("Correo del primer administrador: ").strip().lower()
    if len(email) > 120 or not re.fullmatch(r"[^\s@]+@[^\s@]+\.[^\s@]+", email):
        raise SystemExit("Correo inválido.")
    password = getpass("Contraseña: ")
    if (len(password) < 8 or len(password.encode()) > 72 or
            not all(re.search(rule, password) for rule in ("[A-Z]", "[0-9]", "[^A-Za-z0-9]"))):
        raise SystemExit("Usa 8 caracteres o más, una mayúscula, un número y un símbolo (máximo 72 bytes).")
    if password != getpass("Repite la contraseña: "):
        raise SystemExit("Las contraseñas deben coincidir.")
    hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt(rounds=12)).decode()
    result = subprocess.run(["mysql", "--default-character-set=utf8mb4", "-NB", "roastlab_final"],
        input=f"""
        LOCK TABLES usuarios WRITE, roles READ;
        SET @admin = (SELECT id_rol FROM roles WHERE nombre='administración');
        SET @exists = (SELECT COUNT(*) FROM usuarios WHERE id_rol=@admin);
        INSERT INTO usuarios (nombre,apellido,correo,password_hash,id_rol,activo)
        SELECT 'Administración','RoastLab',{sql_text(email)},{sql_text(hashed)},@admin,1
        WHERE @admin IS NOT NULL AND @exists=0;
        SELECT ROW_COUNT();
        UNLOCK TABLES;
        """, text=True, check=True, stdout=subprocess.PIPE)
    if result.stdout.strip() == "1":
        print("Administrador creado. Ya puedes iniciar sesión.")
    else:
        print("Sin cambios. Comprueba que Flyway creó el rol y si ya hay administrador.")

if __name__ == "__main__":
    main()
