package com.example.karflex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.karflex.data.entities.Rental
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertARental(rental: Rental);

    @Query("SELECT * FROM rentals")
    fun getAllRental() : Flow<List<Rental>>;

    @Query("SELECT * FROM rentals WHERE RentalId = :rentalId")
    suspend fun getRentalById(rentalId: Int) : Rental?;

}