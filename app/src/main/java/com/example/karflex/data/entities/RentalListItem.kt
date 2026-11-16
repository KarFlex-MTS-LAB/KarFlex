package com.example.karflex.data.entities

data class RentalListItem(
    val RentalId: Int,
    val UserId: Int,
    val VehicleId: Int,
    val duration: Int,
    val startDate: String,
    val status: String,
    val modelName: String,
    val brand: String,
    val image: String
)
