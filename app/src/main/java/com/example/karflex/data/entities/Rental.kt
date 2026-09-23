package com.example.karflex.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Rentals",
        foreignKeys = [
            ForeignKey(
                entity = User::class,
                parentColumns = ["UserId"],
                childColumns = ["UserId"],
                onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                entity = Vehicle::class,
                parentColumns = ["VehicleId"],
                childColumns = ["VehicleId"],
                onDelete = ForeignKey.CASCADE
            )
        ]
    )
data class Rental(
    @PrimaryKey(autoGenerate = true)
    val RentalId: Int = 0,
    val UserId: Int,
    val VehicleId: Int,
    val duration: Int,
    val startDate: String,
    val status: String
)