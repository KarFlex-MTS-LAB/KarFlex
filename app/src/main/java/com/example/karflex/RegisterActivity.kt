package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.karflex.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val mainView = findViewById<LinearLayout>(R.id.main)

        val initialLeft = mainView.paddingLeft
        val initialTop = mainView.paddingTop
        val initialRight = mainView.paddingRight
        val initialBottom = mainView.paddingBottom

        val tvToLogin = findViewById<TextView>(R.id.tvToLogin)
        val etUsername = findViewById<EditText>(R.id.etRegUsername)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etPhoneNumber = findViewById<EditText>(R.id.etRegPhoneNumber)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            if(username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO){
                val existingUser = AppDatabase.getDatabase(this@RegisterActivity).UserDao().getUserByUsername(username)

                if(existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
            }

            if(password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(phoneNumber.length < 10) {
                Toast.makeText(this, "Phone number must be at least 10 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(username = username, password = password, phoneNumber = phoneNumber)
            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(this@RegisterActivity).UserDao().insertAUser(newUser)
            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}