package com.example.karflex.data.repository
import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.karflex.AppDatabase
import com.example.karflex.data.entities.Vehicle
import com.example.karflex.data.entities.VehicleCard
import com.example.karflex.data.entities.VehicleDetail
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import com.google.gson.annotations.SerializedName
import org.json.JSONArray

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

    suspend fun initialVehicle() = suspendCancellableCoroutine<Unit> { continuation ->
        val request = JsonArrayRequest(
            Request.Method.GET,
            JSON_URL,
            null,
            { response: JSONArray ->
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching {
                        for (index in 0 until response.length()) {
                            val item = response.getJSONObject(index)
                            AppDatabase.getDatabase(context).VehicleDao().insertAVehicle(Vehicle(
                                VehicleId = item.getInt("id"), modelName = item.getString("model_name"),
                                type = item.getString("type"), brand = item.getString("brand"),
                                pricePerDay = item.getInt("price_per_day"), transmission = item.getString("transmission"),
                                image = item.getString("image_url"), description = item.getString("description")
                            ))
                        }
                    }
                    if (continuation.isActive) continuation.resume(Unit)
                }
            },
            { if (continuation.isActive) continuation.resume(Unit) }
        )
        Volley.newRequestQueue(context).add(request)
        continuation.invokeOnCancellation { request.cancel() }
    }

    companion object {
        private const val JSON_URL = "https://gist.githubusercontent.com/axelkrnwn/01c506ac60735f553af951e550634b59/raw/08489b2ff76e1356a6df38d09948716628024195/karflex.json"
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
