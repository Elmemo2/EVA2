package com.example.eva2.data.model

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)
