package com.example.karflex.data.dao
import com.example.karflex.data.repository.ItemDTO
import retrofit2.http.GET

interface ApiService {

    @GET("")
    suspend fun getVehicles(): List<ItemDTO>
}