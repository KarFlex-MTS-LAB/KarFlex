package com.example.karflex.data.repository
import android.content.Context
import com.example.karflex.AppDatabase
import com.example.karflex.data.dao.UserDAO
import com.example.karflex.data.entities.User

data class RegisterRequest (
    val username: String,
    val password: String,
    val phoneNumber: String
)

data class LoginRequest (
    val username: String,
    val password: String
)

class UserRepository(private val context: Context) {

    suspend fun registerUser(request: RegisterRequest){
        try {
            val findUsername = AppDatabase.getDatabase(context).UserDao().getUserByUsername(request.username);

            if(findUsername != null) {
                println("username must be unique!");
                return;
            }

            val user = User(
                username = request.username,
                password = request.password,
                phoneNumber = request.phoneNumber
            )

            AppDatabase.getDatabase(context).UserDao().insertAUser(user);

            println("Successfully registered user: ${user.username}");
        } catch (e: Exception){
            print("DEBUG LOG ERROR DURING REGISTER: ${e.message}");
        }
    }

    suspend fun loginUser(request: LoginRequest): String?{
        try {

            val username = request.username;

            val user = AppDatabase.getDatabase(context).UserDao().getUserByUsername(username);

            if(user == null){
                println("User not found!");
                return null;
            }

            if(user.password != request.password){
                println("Wrong password!");
                return null;
            }

            println("Successfully logged in user: ${user.username}");

            return user.username;
        } catch (e: Exception) {
            print("DEBUG LOG ERROR DURING LOGIN: ${e.message}");

            return null;
        }

    }

}
