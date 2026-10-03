import os

import mysql.connector
import random as rnd
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from werkzeug.security import check_password_hash, generate_password_hash

load_dotenv(override=True)

app = Flask(__name__)

DB_CONFIG = {
    "host": "eva2-db.c6ddgi5q5mhs.us-east-1.rds.amazonaws.com",
    "port": 3306,
    "database": os.getenv("DB_NAME", "eva2"),
    "user": os.getenv("DB_USER", "admin"),
    "password": os.getenv("DB_PASSWORD", ""),
}


def get_connection():
    return mysql.connector.connect(
        **DB_CONFIG,
        connection_timeout=10,
    )


@app.get("/api/health")
def health():
    return jsonify({
        "service": "EVA 2 API",
        "status": "ok",
        "version": "1.0.0",
    })

def save_vehicle_data(rpm, speed, temperature, voltage, fuel):
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        sql = """
        INSERT INTO vehicle_data
        (rpm, speed, temperature, voltage,
        fuel, vehicle_status, engine_status)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        
        cursor.execute(sql, (
            rpm,
            speed,
            temperature,
            voltage,
            fuel,
            "CONNECTED",
            "NORMAL"
        ))
        
        conn.commit()
        cursor.close()
        conn.close()
        
        print("Dato guardado en vehicle_data")
        
    except Exception as e:
        print("Error guardado vehicle_data:", e)
    

@app.get("/api/vehicle/status")
def vehicle_status():
    rpm = rnd.randint(1500, 3000)
    speed = rnd.randint(40, 100)
    temperature = rnd.randint(80, 95)
    voltage = round(rnd.uniform(13.5, 14.4), 1)
    fuel = rnd.randint(60, 75)
    
    save_vehicle_data(
        rpm,
        speed,
        temperature,
        voltage,
        fuel
    )
    
    return jsonify({
        "vehicleStatus": "CONNECTED",
        "rpm": rpm,
        "speed": speed,
        "temperature": temperature,
        "voltage": voltage,
        "fuel": fuel,
        "engineStatus": "NORMAL",
        

    })
    
@app.get("/api/vehicle/history")
def vehicle_history():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute("""
            SELECT
                id,
                rpm,
                speed,
                temperature,
                voltage,
                fuel,
                vehicle_status,
                engine_status,
                created_at
            FROM vehicle_data
            ORDER BY id DESC
            LIMIT 50
        """)
        
        data = cursor.fetchall()
        
        cursor.close()
        conn.close()
        
        return jsonify(data)
        
    except Exception as e:
        print("error obtenido historial:", e)
        return jsonify({
            "error": "No se pudo obtener el historial"
        }), 500


@app.post("/api/register")
def register():
    data = request.get_json(silent=True) or {}

    nombre = str(data.get("nombre", "")).strip()
    rut = str(data.get("rut", "")).strip()
    password = str(data.get("password", ""))

    if not nombre or not rut or not password:
        return jsonify({
            "success": False,
            "message": "Nombre, RUT y contraseña son obligatorios.",
        }), 400

    if len(password) < 6:
        return jsonify({
            "success": False,
            "message": "La contraseña debe tener al menos 6 caracteres.",
        }), 400

    connection = None
    cursor = None

    try:
        connection = get_connection()
        cursor = connection.cursor(dictionary=True)

        cursor.execute(
            "SELECT id FROM users WHERE rut = %s",
            (rut,),
        )

        if cursor.fetchone():
            return jsonify({
                "success": False,
                "message": "El RUT ya está registrado.",
            }), 409

        password_hash = generate_password_hash(password)

        cursor.execute(
            """
            INSERT INTO users (nombre, rut, password_hash)
            VALUES (%s, %s, %s)
            """,
            (nombre, rut, password_hash),
        )

        connection.commit()

        return jsonify({
            "success": True,
            "message": "Registro realizado correctamente.",
        }), 201

    except mysql.connector.Error as error:
        return jsonify({
            "success": False,
            "message": "No fue posible registrar el usuario.",
            "detail": str(error),
        }), 500

    finally:
        if cursor is not None:
            cursor.close()
        if connection is not None and connection.is_connected():
            connection.close()


@app.post("/api/login")
def login():
    data = request.get_json(silent=True) or {}

    rut = str(data.get("rut", "")).strip()
    password = str(data.get("password", ""))

    if not rut or not password:
        return jsonify({
            "success": False,
            "message": "RUT y contraseña son obligatorios.",
        }), 400

    connection = None
    cursor = None

    try:
        connection = get_connection()
        cursor = connection.cursor(dictionary=True)

        cursor.execute(
            """
            SELECT id, nombre, rut, password_hash
            FROM users
            WHERE rut = %s
            """,
            (rut,),
        )

        user = cursor.fetchone()

        if user is None or not check_password_hash(
            user["password_hash"],
            password,
        ):
            return jsonify({
                "success": False,
                "message": "RUT o contraseña incorrectos.",
            }), 401

        return jsonify({
            "success": True,
            "message": "Inicio de sesión correcto.",
            "user": {
                "id": user["id"],
                "nombre": user["nombre"],
                "rut": user["rut"],
            },
        })

    except mysql.connector.Error as error:
        return jsonify({
            "success": False,
            "message": "No fue posible iniciar sesión.",
            "detail": str(error),
        }), 500

    finally:
        if cursor is not None:
            cursor.close()
        if connection is not None and connection.is_connected():
            connection.close()


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
