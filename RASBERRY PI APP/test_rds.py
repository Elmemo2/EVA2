import mysql.connector
from getpass import getpass

HOST = "eva2-db.c6ddgi5q5mhs.us-east-1.rds.amazonaws.com"
PORT = 3306
USER = "admin"

print("======================================")
print("   EVA 2 - PRUEBA DE CONEXION RDS")
print("======================================")
print(f"Servidor: {HOST}")
print(f"Puerto:   {PORT}")
print(f"Usuario:  {USER}")
print()

password = getpass("Contraseña de RDS: ")

try:
    connection = mysql.connector.connect(
        host=HOST,
        port=PORT,
        user=USER,
        password=password,
        connection_timeout=10
    )

    print()
    print("======================================")
    print("CONEXION A AWS RDS EXITOSA")
    print("======================================")
    print(f"Servidor MySQL: {connection.server_host}")
    print(f"Version MySQL:  {connection.get_server_info()}")

    cursor = connection.cursor()
    cursor.execute("SELECT VERSION()")
    version = cursor.fetchone()[0]
    print(f"SELECT VERSION(): {version}")

    cursor.close()
    connection.close()

except mysql.connector.Error as error:
    print()
    print("======================================")
    print("ERROR DE CONEXION A AWS RDS")
    print("======================================")
    print(error)
