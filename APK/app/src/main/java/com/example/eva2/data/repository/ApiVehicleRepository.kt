package com.example.eva2.data.repository

import com.example.eva2.data.model.VehicleData
import com.example.eva2.data.model.VehicleHistory
import com.example.eva2.data.remote.ApiConfig
import com.example.eva2.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiVehicleRepository(
    private val apiService: ApiService = ApiConfig.apiService
) : VehicleRepository {

    override suspend fun getVehicleData(): Result<VehicleData> {
        return try {
            val data = apiService.getVehicleStatus()
            Result.success(data)
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al conectar con el servidor. Inténtalo nuevamente."))
        } catch (e: ConnectException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: HttpException) {
            Result.failure(Exception("No se pudieron obtener los datos del vehículo desde el servidor."))
        } catch (e: IOException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor. Inténtalo nuevamente."))
        }
    }

    override suspend fun getVehicleHistory(): Result<List<VehicleHistory>> {
        return try {
            val history = apiService.getVehicleHistory()
            Result.success(history)
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al conectar con el servidor. Inténtalo nuevamente."))
        } catch (e: ConnectException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: HttpException) {
            Result.failure(Exception("No se pudo obtener el historial de telemetría desde el servidor."))
        } catch (e: IOException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifica tu conexión WiFi."))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo obtener el historial de telemetría. Inténtalo nuevamente."))
        }
    }
}
