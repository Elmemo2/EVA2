from dotenv import load_dotenv
import os

load_dotenv()

print("=== EVA 2 - COMPROBACION .ENV ===")
print("HOST:", os.getenv("DB_HOST"))
print("PORT:", os.getenv("DB_PORT"))
print("DATABASE:", os.getenv("DB_NAME"))
print("USER:", os.getenv("DB_USER"))
print("PASSWORD:", "CONFIGURADA" if os.getenv("DB_PASSWORD") else "FALTA")
