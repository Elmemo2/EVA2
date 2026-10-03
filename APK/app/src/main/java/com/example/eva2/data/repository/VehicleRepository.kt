package com.example.eva2.data.repository

import com.example.eva2.data.model.VehicleData
import com.example.eva2.data.model.VehicleHistory

interface VehicleRepository {
    suspend fun getVehicleData(): Result<VehicleData>
    suspend fun getVehicleHistory(): Result<List<VehicleHistory>>
}
