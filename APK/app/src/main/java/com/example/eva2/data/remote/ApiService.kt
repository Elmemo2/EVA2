package com.example.eva2.data.remote

import com.example.eva2.data.model.AuthResponse
import com.example.eva2.data.model.HealthResponse
import com.example.eva2.data.model.LoginRequest
import com.example.eva2.data.model.RegisterRequest
import com.example.eva2.data.model.VehicleData
import com.example.eva2.data.model.VehicleHistory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @GET("api/health")
    suspend fun checkHealth(): HealthResponse

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/vehicle/status")
    suspend fun getVehicleStatus(): VehicleData

    @GET("api/vehicle/history")
    suspend fun getVehicleHistory(): List<VehicleHistory>
}
