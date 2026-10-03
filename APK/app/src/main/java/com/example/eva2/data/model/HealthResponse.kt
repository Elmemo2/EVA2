package com.example.eva2.data.model

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    @SerializedName("service")
    val service: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("version")
    val version: String
)
