# EVA 2 API

Backend inicial de EVA 2 para Raspberry Pi OS.

## Arquitectura

Android EVA 2
    |
    | HTTP/JSON por WiFi
    v
Raspberry Pi - Flask API
    |
    | futura etapa
    v
AWS RDS MySQL

## Estado actual

Esta versión utiliza datos simulados y usuarios en memoria.

Endpoints:

- GET `/api/health`
- GET `/api/vehicle/status`
- POST `/api/register`
- POST `/api/login`

La conexión con AWS RDS MySQL se implementará posteriormente. No se incluyen credenciales reales.

## Instalación en Raspberry Pi

Con el proyecto ubicado en `/home/pi/eva2-api`:

```bash
cd ~/eva2-api
source venv/bin/activate
pip install -r requirements.txt
python app.py
```

La API quedará disponible en:

`http://192.168.0.81:5000`

Si la IP del Raspberry cambia, usar la IP actual obtenida con `hostname -I`.

## Pruebas

Salud:

```bash
curl http://127.0.0.1:5000/api/health
```

Estado del vehículo:

```bash
curl http://127.0.0.1:5000/api/vehicle/status
```

Registro:

```bash
curl -X POST http://127.0.0.1:5000/api/register   -H "Content-Type: application/json"   -d '{"nombre":"Juan Perez","rut":"12345678-9","password":"Test1234"}'
```

Login:

```bash
curl -X POST http://127.0.0.1:5000/api/login   -H "Content-Type: application/json"   -d '{"rut":"12345678-9","password":"Test1234"}'
```

Desde Windows, reemplazar `127.0.0.1` por la IP del Raspberry:

`http://192.168.0.81:5000/api/health`

## Seguridad

No guardar contraseñas en texto plano. La API usa `generate_password_hash` y `check_password_hash` para la simulación.

No incluir `.env`, credenciales AWS, contraseñas de RDS, Access Keys, Secret Keys ni Session Tokens en GitHub.
