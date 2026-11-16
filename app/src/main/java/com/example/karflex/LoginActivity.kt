package com.example.karflex

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.karflex.utils.SmsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private var pendingPhoneNumber: String? = null
    private var pendingUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val mainView = findViewById<LinearLayout>(R.id.main)

        val initialLeft = mainView.paddingLeft
        val initialTop = mainView.paddingTop
        val initialRight = mainView.paddingRight
        val initialBottom = mainView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            )
            insets
        }

        val btnReturn = findViewById<ImageButton>(R.id.btnReturn)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnReturn.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existingUser = AppDatabase.getDatabase(this@LoginActivity).UserDao().getUserByUsername(username)
                if(existingUser == null) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                if (password != existingUser.password) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val phoneNumber = existingUser.phoneNumber

                withContext(Dispatchers.Main) {
                    if (ContextCompat.checkSelfPermission(
                            this@LoginActivity,
                            android.Manifest.permission.SEND_SMS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        pendingPhoneNumber = phoneNumber
                        pendingUsername = existingUser.username
                        ActivityCompat.requestPermissions(
                            this@LoginActivity,
                            arrayOf(android.Manifest.permission.SEND_SMS),
                            SMS_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        sendOtpAndOpen(phoneNumber, existingUser.username)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != SMS_PERMISSION_REQUEST_CODE) return

        val phoneNumber = pendingPhoneNumber
        val username = pendingUsername
        pendingPhoneNumber = null
        pendingUsername = null

        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED && phoneNumber != null && username != null) {
            sendOtpAndOpen(phoneNumber, username)
        } else {
            Toast.makeText(this, "SMS permission is required to send the login OTP", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendOtpAndOpen(phoneNumber: String, username: String) {
        val otpCode = Random.nextInt(1000, 10000).toString()
        SmsUtils.sentOtp(this, phoneNumber, otpCode)

        startActivity(Intent(this, OtpActivity::class.java).apply {
            putExtra("KODE_OTP_ASLI", otpCode)
            putExtra("NOMOR_HP", phoneNumber)
            putExtra("USERNAME", username)
        })
    }

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 100
    }
}
