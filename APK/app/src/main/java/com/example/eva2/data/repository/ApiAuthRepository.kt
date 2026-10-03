package com.example.eva2.data.repository

import com.example.eva2.data.model.AuthResponse
import com.example.eva2.data.model.LoginRequest
import com.example.eva2.data.model.RegisterRequest
import com.example.eva2.data.remote.ApiConfig
import com.example.eva2.data.remote.ApiService
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiAuthRepository(
    private val apiService: ApiService = ApiConfig.apiService
) : AuthRepository {

    override suspend fun login(rut: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(rut = rut, password = password))
            Result.success(response)
        } catch (e: HttpException) {
            val errorResponse = parseErrorBody(e)
            val userMessage = errorResponse?.message?.takeIf { it.isNotBlank() }
                ?: "El RUT o la contraseña son incorrectos."
            Result.success(
                AuthResponse(
                    success = false,
                    message = userMessage
                )
            )
        } catch (e: SocketTimeoutException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Inténtalo nuevamente."
                )
            )
        } catch (e: UnknownHostException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: ConnectException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: IOException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: Exception) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Inténtalo nuevamente."
                )
            )
        }
    }

    private fun parseErrorBody(e: HttpException): AuthResponse? {
        return try {
            val errorJson = e.response()?.errorBody()?.string()
            if (!errorJson.isNullOrEmpty()) {
                Gson().fromJson(errorJson, AuthResponse::class.java)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun register(nombre: String, rut: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(nombre = nombre, rut = rut, password = password)
            )
            Result.success(response)
        } catch (e: HttpException) {
            val errorResponse = parseErrorBody(e)
            val userMessage = errorResponse?.message?.takeIf { it.isNotBlank() }
                ?: "Este RUT ya está registrado."
            Result.success(
                AuthResponse(
                    success = false,
                    message = userMessage
                )
            )
        } catch (e: SocketTimeoutException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Inténtalo nuevamente."
                )
            )
        } catch (e: UnknownHostException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: ConnectException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: IOException) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                )
            )
        } catch (e: Exception) {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "No se pudo conectar con el servidor. Inténtalo nuevamente."
                )
            )
        }
    }
}
