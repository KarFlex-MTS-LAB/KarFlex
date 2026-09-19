package com.example.karflex
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Rentals")
data class Rental(
    @PrimaryKey (autoGenerate = true)
    val RentalId: Int = 0,
    val UserId: Int,
    val VehicleId: Int,
    val duration: Int,
    val startDate: String,
    val status: String
)
