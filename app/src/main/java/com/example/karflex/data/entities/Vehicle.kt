package com.example.karflex.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName =  "Vehicle")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val VehicleId: Int = 0,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String
)