package com.example.eva2.data.model

import com.google.gson.annotations.SerializedName

data class VehicleData(
    @SerializedName("rpm")
    val rpm: Int = 1850,
    @SerializedName("speed")
    val speed: Int = 62,
    @SerializedName("temperature")
    val engineTemperature: Int = 87,
    @SerializedName("voltage")
    val voltage: Double = 13.8,
    @SerializedName("fuel")
    val fuelLevel: Int = 72,
    @SerializedName("vehicleStatus")
    val vehicleStatus: String = "CONECTADO",
    @SerializedName("engineStatus")
    val engineStatus: String = "NORMAL"
)
