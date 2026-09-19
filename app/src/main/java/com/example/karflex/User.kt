package com.example.karflex
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Users")
data class User (
    @PrimaryKey (autoGenerate = true)
    val UserId: Int = 0,
    val username: String,
    val password: String,
    val phoneNumber: String
)