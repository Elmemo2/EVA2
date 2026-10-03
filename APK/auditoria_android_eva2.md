# INFORME DE AUDITORÍA TÉCNICA — PROYECTO ANDROID EVA 2

---

## 1. RESUMEN GENERAL

- **Estado Actual**: El proyecto Android **EVA 2** posee una arquitectura moderna, limpia y funcional basada en **Kotlin + Jetpack Compose + Material 3 + MVVM + Repository Pattern + Retrofit**.
- **Etapas Verificadas**:
  - **ETAPA 1 (`/api/health`)**: ✅ **Totalmente Implementada y Conectada**.
  - **ETAPA 2 (Login Real `POST /api/login`)**: ✅ **Totalmente Implementada y Conectada**.
  - **ETAPA 3 (Registro Real `POST /api/register`)**: ✅ **Totalmente Implementada y Conectada**.
  - **ETAPA 4 (Conectividad Externa)**: 🟡 **Parcial**. La arquitectura de Android está **100% preparada** gracias a la centralización de `BASE_URL` en `ApiConfig.kt`. La infraestructura del túnel externo (Cloudflare Tunnel) se encuentra pendiente de despliegue en la Raspberry Pi.
  - **Monitoreo Vehicular (`GET /api/vehicle/status`)**: 🟡 **Parcial**. El modelo, endpoint e interfaz `ApiVehicleRepository` están creados, pero `MonitoringViewModel` actualmente utiliza `MockVehicleRepository` por defecto.

---

## 2. ESTRUCTURA DEL PROYECTO

- **`AndroidManifest.xml`**: Manifiesto principal. Declara el permiso `android.permission.INTERNET`, habilita `usesCleartextTraffic="true"` para tráfico HTTP local y configura la actividad principal `MainActivity`.
- **`build.gradle.kts`**: Configuración de dependencias (Jetpack Compose BOM, Material 3, Navigation Compose, ViewModel Compose, Retrofit 2 y Gson Converter).
- **`MainActivity.kt`**: Actividad única (Single Activity) que habilita Edge-to-Edge e inicializa `AppNavigation()` dentro del tema `EVA2Theme`.
- **`com.example.eva2.navigation.AppNavigation.kt`**: Grafo de navegación centralizado (`Screen.LOGIN`, `Screen.REGISTER`, `Screen.MONITORING`).
- **`com.example.eva2.data.model`**:
  - `LoginRequest.kt`: DTO para solicitud de inicio de sesión (`rut`, `password`).
  - `RegisterRequest.kt`: DTO para solicitud de registro (`nombre`, `rut`, `password`).
  - `AuthResponse.kt`: DTO para respuestas de autenticación (`success`, `message`, `user`).
  - `HealthResponse.kt`: DTO para estado del servidor (`service`, `status`, `version`).
  - `User.kt`: Modelo de entidad del usuario (`id`, `nombre`, `rut`, `password`).
  - `VehicleData.kt`: DTO de telemetría del vehículo (`rpm`, `speed`, `engineTemperature`, `voltage`, `fuelLevel`, `vehicleStatus`, `engineStatus`).
- **`com.example.eva2.data.remote`**:
  - `ApiConfig.kt`: Configuración singleton de Retrofit. Centraliza la constante `BASE_URL = "http://192.168.0.81:5000/"`.
  - `ApiService.kt`: Interfaz Retrofit que declara los 4 endpoints HTTP de la API REST.
- **`com.example.eva2.data.repository`**:
  - `AuthRepository.kt`: Interfaz de operaciones de autenticación.
  - `ApiAuthRepository.kt`: Implementación remota con captura de excepciones de red y traducción a mensajes amigables.
  - `MockAuthRepository.kt`: Repositorio simulado en memoria (conservado sin romper arquitectura).
  - `HealthRepository.kt` / `ApiHealthRepository.kt`: Interfaz e implementación remota para verificación de salud del backend.
  - `VehicleRepository.kt` / `ApiVehicleRepository.kt` / `MockVehicleRepository.kt`: Repositorio e implementaciones (remota y mock) para datos del vehículo.
- **`com.example.eva2.ui.components`**:
  - `ApiHealthStatusCard.kt`: Componente Compose para visualizar el estado de conexión del backend en tiempo real.
- **`com.example.eva2.ui.screens`**:
  - `LoginScreen.kt`: Pantalla de inicio de sesión.
  - `RegisterScreen.kt`: Pantalla de registro de usuario.
  - `MonitoringScreen.kt`: Dashboard de métricas del vehículo.
- **`com.example.eva2.ui.viewmodel`**:
  - `AuthViewModel.kt`: ViewModel de autenticación (por defecto usa `ApiAuthRepository`).
  - `HealthViewModel.kt`: ViewModel para comprobación de la API (por defecto usa `ApiHealthRepository`).
  - `MonitoringViewModel.kt`: ViewModel para monitoreo (por defecto usa `MockVehicleRepository`).

---

## 3. AUTENTICACIÓN

- **Login**: ✅ **Implementado**. Valida RUT, gestiona estados de carga, alterna visibilidad de contraseña y realiza la llamada remota `authRepository.login()`.
- **Registro**: ✅ **Implementado**. Valida campos obligatorios, coincidencia de contraseñas, formato de RUT y realiza la llamada remota `authRepository.register()`.
- **Modelos**: ✅ **Implementados** (`LoginRequest`, `RegisterRequest`, `AuthResponse`, `User`).
- **ViewModels**: ✅ **Implementados** (`AuthViewModel` gestiona los estados mediante `StateFlow<AuthUiState>`).
- **Repository**: ✅ **Implementados** (`AuthRepository`, `ApiAuthRepository`, `MockAuthRepository`).
- **ApiService**: ✅ **Implementados** (`@POST("api/login")` y `@POST("api/register")`).
- **Manejo de Sesión**: 🟡 **Parcial**. Al autenticarse correctamente, el flujo redirige a la pantalla de Monitoreo. Sin embargo, no existe almacenamiento persistente en memoria local (como DataStore) para mantener la sesión abierta tras reiniciar la app.
- **Navegación después del Login**: ✅ **Implementada** en `AppNavigation.kt` mediante `popUpTo(Screen.LOGIN) { inclusive = true }`.

---

## 4. COMUNICACIÓN CON BACKEND

| Método HTTP | Ruta | Request | Response | Archivo donde se define | Repository que lo utiliza | ViewModel que lo utiliza | Pantalla que lo utiliza |
|---|---|---|---|---|---|---|---|
| `GET` | `api/health` | *(Vacío)* | `HealthResponse` | `ApiService.kt` | `ApiHealthRepository.kt` | `HealthViewModel.kt` | `LoginScreen.kt` (a través de `ApiHealthStatusCard.kt`) |
| `POST` | `api/login` | `LoginRequest` | `AuthResponse` | `ApiService.kt` | `ApiAuthRepository.kt` | `AuthViewModel.kt` | `LoginScreen.kt` |
| `POST` | `api/register` | `RegisterRequest` | `AuthResponse` | `ApiService.kt` | `ApiAuthRepository.kt` | `AuthViewModel.kt` | `RegisterScreen.kt` |
| `GET` | `api/vehicle/status` | *(Vacío)* | `VehicleData` | `ApiService.kt` | `ApiVehicleRepository.kt` | `MonitoringViewModel.kt` *(no inyectado por defecto)* | `MonitoringScreen.kt` |

---

## 5. CONFIGURACIÓN DE RED

- **Dónde está definida `BASE_URL`**: `ApiConfig.kt`.
- **Valor actual**: `const val BASE_URL = "http://192.168.0.81:5000/"`.
- **¿Está centralizada?**: ✅ **SÍ, 100% centralizada**. Ninguna pantalla, ViewModel ni repositorio contiene URLs o direcciones IP escritas directamente.
- **Referencias directas a `192.168.0.81`**:
  - Únicamente la constante `BASE_URL` en `ApiConfig.kt`.
  - Un mensaje descriptivo de error en `ApiHealthRepository.kt` para informar al usuario sobre la red local.
- **URLs duplicadas**: ❌ Ninguna.
- **Configuración para HTTPS**: `AndroidManifest.xml` cuenta con `android:usesCleartextTraffic="true"`. Al reemplazar la `BASE_URL` por una dirección HTTPS (ej. Cloudflare Tunnel), Retrofit utilizará SSL/TLS automáticamente.
- **Cambio Desarrollo / Producción**: No requiere refactorizaciones. Solo basta con modificar el valor de la constante `BASE_URL` en `ApiConfig.kt`.

---

## 6. MANEJO DE ERRORES

- **Conversión a mensajes amigables**: ✅ **Totalmente implementado** en `ApiAuthRepository` y `ApiHealthRepository`.
- **Mapeo por tipo de excepción**:
  - **Timeout (`SocketTimeoutException`)**: *"No se pudo conectar con el servidor. Inténtalo nuevamente."*
  - **Servidor no disponible / Sin WiFi (`UnknownHostException`, `ConnectException`, `IOException`)**: *"No se pudo conectar con el servidor. Verifica tu conexión WiFi."*
  - **Credenciales incorrectas / Error HTTP 401 o 400**: *"El RUT o la contraseña son incorrectos."* / *"Este RUT ya está registrado."*
  - **Excepciones no controladas / HTTP 500**: Capturadas en bloque `catch (e: Exception)` evitando el cierre inesperado (crash) de la app.
  - **Filtro de seguridad**: Se garantiza que **ningún** código HTTP (401, 500) ni stack trace técnico de Retrofit se muestre al usuario final.

---

## 7. ARQUITECTURA

```
                   [ UI Jetpack Compose ]
               (Login, Register, Monitoring)
                             │
                             ▼
                      [ ViewModels ]
         (AuthViewModel, HealthViewModel, MonitoringViewModel)
                             │
                             ▼
                     [ Repositories ]
      (ApiAuthRepository, ApiHealthRepository, ApiVehicleRepository)
                             │
                             ▼
                    [ Retrofit Engine ]
                (ApiConfig.kt + ApiService.kt)
                             │
                             ▼
                 [ Raspberry Pi OS Flask ]
                 (http://192.168.0.81:5000)
                             │
                             ▼
                   [ AWS RDS MySQL DB ]
```

**Desviación Detectada**:
- `MonitoringViewModel` inicializa por defecto `MockVehicleRepository` en lugar de `ApiVehicleRepository(ApiConfig.apiService)`.

---

## 8. PANTALLAS

1. **`LoginScreen`**: ✅ **Conectada al Backend Real**.
   - Muestra el estado de salud de la API en tiempo real (`ApiHealthStatusCard`).
   - Envía solicitudes de inicio de sesión reales a `POST /api/login`.
2. **`RegisterScreen`**: ✅ **Conectada al Backend Real**.
   - Envía registro de usuarios reales a `POST /api/register`.
3. **`MonitoringScreen`**: 🟡 **Parcialmente Conectada (Interfaz lista, datos simulados)**.
   - El dashboard Compose está completamente programado y funcional, pero consume los datos simulados de `MockVehicleRepository`.

---

## 9. DATOS DEL VEHÍCULO (`/api/vehicle/status`)

- **¿Android lo consume actualmente?**: No en la ejecución por defecto. El endpoint está declarado en `ApiService.kt` y la clase `ApiVehicleRepository.kt` está creada, pero `MonitoringViewModel.kt` utiliza el mock.
- **Modelo utilizado**: `VehicleData` (`rpm`, `speed`, `engineTemperature`, `voltage`, `fuelLevel`, `vehicleStatus`, `engineStatus`).
- **Repository que lo maneja**: `VehicleRepository` (`ApiVehicleRepository` / `MockVehicleRepository`).
- **Valores simulados**: 1850 RPM, 62 km/h, 87 °C, 13.8 V, 72 % combustible, Estado CONECTADO, Motor NORMAL.
- **Qué falta para conectarlo al flujo real**:
  1. Cambiar la asignación del repositorio en `MonitoringViewModel`:
     `private val vehicleRepository: VehicleRepository = ApiVehicleRepository(ApiConfig.apiService)`
  2. Confirmar que el endpoint Flask devuelva las métricas con los nombres de clave de `VehicleData`.

---

## 10. SEGURIDAD

- **Credenciales hardcodeadas**: ❌ Ninguna credencial de RDS ni secretos de servidor existen dentro del proyecto Android.
- **Contraseñas**: Transmitidas en el cuerpo JSON de `LoginRequest` y `RegisterRequest`.
- **Tokens**: ❌ No se utiliza autenticación basada en tokens JWT.
- **Configuración de Cleartext HTTP**: `android:usesCleartextTraffic="true"` activado para la IP local.
- **Almacenamiento Local de Sesión**: ❌ No existe persistencia local de sesión tras el cierre de la app.

---

## 11. DEPENDENCIAS

Todas las dependencias están declaradas en `app/build.gradle.kts`:
- **Jetpack Compose BOM / UI / Material 3 / Navigation**: Actualizadas y compatibles.
- **Retrofit 2 + Gson Converter**: Funcionando correctamente.
- **Evaluación**: No existen dependencias redundantes, obsoletas ni incompatibles.

---

## 12. TABLA DE ESTADO DEL PROYECTO

| Funcionalidad | Estado | Evidencia / Archivo |
|---|---|---|
| **Health Check (`/api/health`)** | ✅ Implementado | `ApiHealthRepository.kt`, `HealthViewModel.kt`, `ApiHealthStatusCard.kt` |
| **Login (`POST /api/login`)** | ✅ Implementado | `ApiAuthRepository.kt`, `AuthViewModel.kt`, `LoginScreen.kt` |
| **Registro (`POST /api/register`)** | ✅ Implementado | `ApiAuthRepository.kt`, `AuthViewModel.kt`, `RegisterScreen.kt` |
| **API Externa / URL HTTPS** | 🟡 Parcial (Preparado en Android) | Centralizado en `ApiConfig.kt` |
| **Monitoring Dashboard (UI)** | ✅ Implementado | `MonitoringScreen.kt` |
| **Vehicle Status (`GET /api/vehicle/status`)** | 🟡 Parcial (Usa Mock Repository) | `ApiVehicleRepository.kt`, `MonitoringViewModel.kt` |
| **Manejo de Errores Amigables** | ✅ Implementado | `ApiAuthRepository.kt`, `ApiHealthRepository.kt` |
| **Navegación** | ✅ Implementado | `AppNavigation.kt` |
| **Seguridad Básica** | ✅ Implementado | Sin credenciales/secretos en APK |

---

## 13. LO QUE FALTA

1. **Conectar la pantalla de Monitoreo al backend real**: Cambiar el repositorio predeterminado en `MonitoringViewModel.kt` a `ApiVehicleRepository(ApiConfig.apiService)`.
2. **Desplegar el túnel HTTPS externo (Cloudflare Tunnel)**: Ejecutar `cloudflared` en la Raspberry Pi OS y actualizar la constante `BASE_URL` en `ApiConfig.kt` con el nuevo subdominio HTTPS asignado.
3. **Persistencia de sesión (Opcional)**: Implementar DataStore para recordar al usuario autenticado.

---

## 14. PRÓXIMOS PASOS RECOMENDADOS

1. **Paso 1**: Cambiar `MonitoringViewModel` para inyectar `ApiVehicleRepository(ApiConfig.apiService)` y conectar el Monitoreo al backend real.
2. **Paso 2**: Iniciar Cloudflare Tunnel en la Raspberry Pi OS para exponer la API en Internet con HTTPS y probar la respuesta `HTTP 200` en `/api/health` desde red 4G/5G.
3. **Paso 3**: Reemplazar `http://192.168.0.81:5000/` en `ApiConfig.kt` por la nueva URL HTTPS de Cloudflare y realizar las pruebas finales E2E desde un dispositivo Android real sobre datos móviles.
