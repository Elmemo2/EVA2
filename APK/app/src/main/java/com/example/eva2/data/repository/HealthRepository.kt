package com.example.eva2.data.repository

import com.example.eva2.data.model.HealthResponse

interface HealthRepository {
    suspend fun checkHealth(): Result<HealthResponse>
}
