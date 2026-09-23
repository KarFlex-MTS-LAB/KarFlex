package com.example.karflex.data.entities

import android.R

//{
//    "id": 1,
//    "model_name": "Honda Vario 160",
//    "type": "Motorcycle",
//    "brand": "Honda",
//    "price_per_day": 90000,
//    "transmission": "Automatic",
//    "image_url": "https://honda-motor.id/wp-content/uploads/2024/02/New-Honda-Vario-160-CBS-Active-Matte-Black.png",
//    "description": "A nimble scooter with excellent fuel economy, ideal for city mobility."
//},

data class VehicleCard(
    val VehicleId: Int,
    val modelName: String,
    val brand: String,
    val pricePerDay: Int,
    val image: String,
    val status: String?
)
