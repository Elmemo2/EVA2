package com.example.eva2.data.repository

import com.example.eva2.data.model.VehicleData
import com.example.eva2.data.model.VehicleHistory
import kotlinx.coroutines.delay

object MockVehicleRepository : VehicleRepository {

    override suspend fun getVehicleData(): Result<VehicleData> {
        delay(400) // Simular latencia de red
        return Result.success(
            VehicleData(
                rpm = 1850,
                speed = 62,
                engineTemperature = 87,
                voltage = 13.8,
                fuelLevel = 72,
                vehicleStatus = "CONECTADO",
                engineStatus = "NORMAL"
            )
        )
    }

    override suspend fun getVehicleHistory(): Result<List<VehicleHistory>> {
        delay(400) // Simular latencia de red
        return Result.success(
            listOf(
                VehicleHistory(
                    id = 1,
                    rpm = 1850,
                    speed = 62,
                    temperature = 87,
                    voltage = 13.8,
                    fuel = 72,
                    vehicleStatus = "CONNECTED",
                    engineStatus = "NORMAL",
                    createdAt = "2026-10-03 17:00:00"
                )
            )
        )
    }
}
