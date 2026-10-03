package com.example.eva2.data.repository

import com.example.eva2.data.model.AuthResponse
import com.example.eva2.data.model.User
import kotlinx.coroutines.delay
import java.util.UUID

object MockAuthRepository : AuthRepository {

    // Simulación de base de datos en memoria
    private val registeredUsers = mutableMapOf<String, User>()

    private fun normalizeRut(rut: String): String {
        return rut.replace(".", "").replace("-", "").trim().uppercase()
    }

    override suspend fun login(rut: String, password: String): Result<AuthResponse> {
        delay(600) // Simular latencia de red
        val normalized = normalizeRut(rut)
        val user = registeredUsers[normalized]

        return if (user != null && user.password == password) {
            Result.success(
                AuthResponse(
                    success = true,
                    message = "Autenticación correcta",
                    user = user
                )
            )
        } else {
            Result.success(
                AuthResponse(
                    success = false,
                    message = "El RUT o la contraseña son incorrectos."
                )
            )
        }
    }

    override suspend fun register(nombre: String, rut: String, password: String): Result<AuthResponse> {
        delay(600) // Simular latencia de red
        val normalized = normalizeRut(rut)

        if (registeredUsers.containsKey(normalized)) {
            return Result.success(
                AuthResponse(
                    success = false,
                    message = "El RUT ya se encuentra registrado."
                )
            )
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            nombre = nombre.trim(),
            rut = rut.trim(),
            password = password
        )

        registeredUsers[normalized] = newUser

        return Result.success(
            AuthResponse(
                success = true,
                message = "Registro realizado correctamente",
                user = newUser
            )
        )
    }
}
