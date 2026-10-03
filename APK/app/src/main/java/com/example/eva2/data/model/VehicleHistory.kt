package com.example.eva2.data.model

import com.google.gson.annotations.SerializedName

data class VehicleHistory(
    @SerializedName("id")
    val id: Int,

    @SerializedName("rpm")
    val rpm: Int,

    @SerializedName("speed")
    val speed: Int,

    @SerializedName("temperature")
    val temperature: Int,

    @SerializedName("voltage")
    val voltage: Double,

    @SerializedName("fuel")
    val fuel: Int,

    @SerializedName("vehicle_status")
    val vehicleStatus: String,

    @SerializedName("engine_status")
    val engineStatus: String,

    @SerializedName("created_at")
    val createdAt: String
)