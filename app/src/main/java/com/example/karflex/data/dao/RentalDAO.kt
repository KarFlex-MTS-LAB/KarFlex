package com.example.karflex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.karflex.data.entities.Rental
import kotlinx.coroutines.flow.Flow
import com.example.karflex.data.entities.RentalListItem

@Dao
interface RentalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertARental(rental: Rental);

    @Query("SELECT * FROM rentals")
    fun getAllRental() : Flow<List<Rental>>;

    @Query("""
        SELECT r.RentalId, r.UserId, r.VehicleId, r.duration, r.startDate, r.status,
               v.modelName, v.brand, v.image
        FROM rentals r INNER JOIN vehicle v ON v.VehicleId = r.VehicleId
        WHERE r.UserId = :userId ORDER BY r.startDate DESC
    """)
    suspend fun getRentalsForUser(userId: Int): List<RentalListItem>

    @Query("UPDATE rentals SET duration = :duration WHERE RentalId = :rentalId")
    suspend fun updateDuration(rentalId: Int, duration: Int)

    @Query("UPDATE rentals SET status = :status WHERE RentalId = :rentalId")
    suspend fun updateStatus(rentalId: Int, status: String)

    @Query("DELETE FROM rentals WHERE RentalId = :rentalId")
    suspend fun deleteRental(rentalId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM rentals WHERE VehicleId = :vehicleId AND status = 'Active')")
    suspend fun isVehicleRented(vehicleId: Int): Boolean

    @Query("SELECT * FROM rentals WHERE RentalId = :rentalId")
    suspend fun getRentalById(rentalId: Int) : Rental?;

}
