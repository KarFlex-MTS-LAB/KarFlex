package com.example.karflex.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.karflex.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao // <-- Kuncinya ada di sini! Wajib ditambahkan agar Room mengenali interface ini.
interface UserDAO {

   @Query("SELECT * FROM users")
   fun getAllUser(): Flow<List<User>>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertAUser(user: User)

   @Query("SELECT * FROM users WHERE UserId = :userId")
   suspend fun getUserById(userId: Int): User?

   @Query("SELECT * FROM users WHERE username = :username")
   suspend fun getUserByUsername(username: String): User?
}