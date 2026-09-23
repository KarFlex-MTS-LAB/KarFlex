package com.example.karflex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.karflex.data.entities.Vehicle
import com.example.karflex.data.entities.VehicleCard
import com.example.karflex.data.entities.VehicleDetail

@Dao
interface VehicleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAVehicle(vehicle: Vehicle);

    @Query("""
        SELECT v.VehicleId, v.modelName, v.brand, v.pricePerDay, v.image, r.status
        from vehicle v
        LEFT JOIN rentals r ON v.VehicleId = r.VehicleId
    """)
    suspend fun getVehiclecard(): List<VehicleCard>

    @Query("""
        SELECT v.VehicleId, v.modelName, v.type, v.brand, v.pricePerDay, v.transmission, v.image, v.description, r.status
        from vehicle v
        LEFT JOIN rentals r ON v.VehicleId = r.VehicleId
        WHERE v.VehicleId = :vehicleId
    """)
    suspend fun getVehicleById(vehicleId: Int) : VehicleDetail?;

}