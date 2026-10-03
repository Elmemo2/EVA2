package com.example.eva2.data.repository

import com.example.eva2.data.model.HealthResponse
import com.example.eva2.data.remote.ApiConfig
import com.example.eva2.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiHealthRepository(
    private val apiService: ApiService = ApiConfig.apiService
) : HealthRepository {

    override suspend fun checkHealth(): Result<HealthResponse> {
        return try {
            val response = apiService.checkHealth()
            Result.success(response)
        } catch (e: UnknownHostException) {
            Result.failure(
                Exception("No se pudo encontrar la Raspberry Pi en la red (${ApiConfig.BASE_URL}). Revisa si estás conectado al WiFi correcto.")
            )
        } catch (e: ConnectException) {
            Result.failure(
                Exception("No se pudo conectar con la Raspberry Pi (${ApiConfig.BASE_URL}). Verifica que el servidor Flask esté iniciado.")
            )
        } catch (e: SocketTimeoutException) {
            Result.failure(
                Exception("Tiempo de espera agotado al intentar conectar con la Raspberry Pi.")
            )
        } catch (e: HttpException) {
            Result.failure(
                Exception("El servidor respondió con error HTTP ${e.code()}.")
            )
        } catch (e: IOException) {
            Result.failure(
                Exception("Error de red. Asegúrate de estar conectado a la red WiFi local.")
            )
        } catch (e: Exception) {
            Result.failure(
                Exception("Error inesperado al verificar la API: ${e.localizedMessage ?: "desconocido"}")
            )
        }
    }
}
