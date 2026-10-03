import mysql.connector
from getpass import getpass

HOST = "eva2-db.c6ddgi5q5mhs.us-east-1.rds.amazonaws.com"
PORT = 3306
USER = "admin"
DATABASE = "eva2"

print("======================================")
print("     EVA 2 - INICIALIZAR BASE RDS")
print("======================================")
print(f"Servidor: {HOST}")
print(f"Puerto:   {PORT}")
print(f"Usuario:  {USER}")
print(f"Base:     {DATABASE}")
print()

password = getpass("Contraseña de RDS: ")

connection = None
cursor = None

try:
    connection = mysql.connector.connect(
        host=HOST,
        port=PORT,
        user=USER,
        password=password,
        connection_timeout=10
    )

    cursor = connection.cursor()

    cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{DATABASE}`")
    cursor.execute(f"USE `{DATABASE}`")

    cursor.execute(
        '''
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nombre VARCHAR(100) NOT NULL,
            rut VARCHAR(12) NOT NULL UNIQUE,
            password_hash VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        '''
    )

    connection.commit()

    print()
    print("======================================")
    print("BASE DE DATOS INICIALIZADA")
    print("======================================")
    print(f"Base creada/verificada: {DATABASE}")
    print("Tabla creada/verificada: users")
    print("Conexion a RDS: OK")

except mysql.connector.Error as error:
    print()
    print("======================================")
    print("ERROR AL INICIALIZAR RDS")
    print("======================================")
    print(error)

finally:
    if cursor is not None:
        cursor.close()
    if connection is not None and connection.is_connected():
        connection.close()
