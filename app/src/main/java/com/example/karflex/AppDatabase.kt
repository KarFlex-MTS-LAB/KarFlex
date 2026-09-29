package com.example.karflex

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.karflex.data.dao.RentalDAO
import com.example.karflex.data.entities.Rental
import com.example.karflex.data.entities.User
import com.example.karflex.data.entities.Vehicle
import com.example.karflex.data.dao.VehicleDAO
import com.example.karflex.data.dao.UserDAO
import com.example.karflex.data.repository.RentalRepository
import com.example.karflex.data.repository.UserRepository

@Database(entities = [User::class,  Vehicle::class, Rental::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun VehicleDao(): VehicleDAO
    abstract fun UserDao(): UserDAO
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karflex_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}