package com.example.karflex.data.repository
import android.content.Context
import com.example.karflex.AppDatabase
import com.example.karflex.data.client.RetrofitClient
import com.example.karflex.data.dao.ApiService
import com.example.karflex.data.entities.Vehicle
import com.example.karflex.data.entities.VehicleCard
import com.example.karflex.data.entities.VehicleDetail
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ItemDTO(
    val id: Int,
    @SerializedName("model_name") val modelName: String,
    val type: String,
    val brand: String,
    @SerializedName("price_per_day") val pricePerDay: Int,
    val transmission: String,
    @SerializedName("image_url") val image: String,
    val description: String
)

class VehicleRepository(private val context: Context) {

    suspend fun initialVehicle() {
        try {
            val response = RetrofitClient.apiService.getVehicles()

            response.forEach { item ->
                val vehicleEntity = Vehicle(
                    VehicleId = item.id,
                    modelName = item.modelName,
                    type = item.type,
                    brand = item.brand,
                    pricePerDay = item.pricePerDay,
                    transmission = item.transmission,
                    image = item.image,
                    description = item.description
                )

                AppDatabase.getDatabase(context).VehicleDao().insertAVehicle(vehicleEntity)
            }
            println("Successfully insert all data from the url to database")
        } catch (e: Exception) {
            println("ERROR DURING INSERT DATA TO DATABASE: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun getVehicle(): List<VehicleCard> {
        return try {
            val response = AppDatabase.getDatabase(context).VehicleDao().getVehiclecard()
            println("Successfully get all data from database")
            response
        } catch (e: Exception) {
            println("ERROR DURING GET VEHICLE FROM DATABASE: ${e.message}")
            emptyList()
        }
    }

    suspend fun getVehicleDetail(VehicleId: Int): VehicleDetail?{
        try {
            val response = AppDatabase.getDatabase(context).VehicleDao().getVehicleById(VehicleId)
            println("Successfully get detail vehicle")
            return response;
        } catch (e: Exception){
            println("ERROR DURING GET VEHICLE DETAIL: ${e.message}");
            return null;
        }
    }

}
