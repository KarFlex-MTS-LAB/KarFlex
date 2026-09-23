package com.example.karflex.data.entities

data class VehicleDetail(
    val VehicleId: Int = 0,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String,
    val status: String?
)
