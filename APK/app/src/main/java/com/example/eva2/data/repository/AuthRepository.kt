package com.example.eva2.data.repository

import com.example.eva2.data.model.AuthResponse

interface AuthRepository {
    suspend fun login(rut: String, password: String): Result<AuthResponse>
    suspend fun register(nombre: String, rut: String, password: String): Result<AuthResponse>
}
