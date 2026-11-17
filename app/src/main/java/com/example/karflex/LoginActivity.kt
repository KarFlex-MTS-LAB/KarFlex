package com.example.karflex
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private var pendingPhoneNumber: String? = null

    private var pendingUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupWindowInsets()
        setupListeners()
    }

    private fun setupWindowInsets() {
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
    }

    private fun setupListeners() {
        val btnReturn = findViewById<ImageButton>(R.id.btnReturn)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnReturn.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authenticateUser(username, password)
        }
    }

    private fun authenticateUser(username: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUser = AppDatabase.getDatabase(this@LoginActivity)
                .UserDao()
                .getUserByUsername(username)

            withContext(Dispatchers.Main) {
                if (existingUser == null) {
                    Toast.makeText(this@LoginActivity, "Wrong password or username", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                if (password != existingUser.password) {
                    Toast.makeText(this@LoginActivity, "Wrong password or username", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                checkPermissionAndProceed(existingUser.phoneNumber, existingUser.username)
            }
        }
    }

    private fun checkPermissionAndProceed(phoneNumber: String, username: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendOtpAndOpen(phoneNumber, username)
        } else {
            pendingPhoneNumber = phoneNumber
            pendingUsername = username
        }
    }

    private fun sendOtpAndOpen(phoneNumber: String, username: String) {
        val otpCode = Random.nextInt(1000, 10000).toString()

        checkSendSMSPermission(phoneNumber, otpCode)

        val intent = Intent(this, OtpActivity::class.java).apply {
            putExtra("KODE_OTP_ASLI", otpCode)
            putExtra("NOMOR_HP", phoneNumber)
            putExtra("USERNAME", username)
        }
        startActivity(intent)
    }

    fun checkSendSMSPermission(phone: String?, message: String?) {

        if (ActivityCompat.checkSelfPermission(

                this@LoginActivity,

                Manifest.permission.SEND_SMS

            ) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(

                this@LoginActivity,

                arrayOf<String>(Manifest.permission.SEND_SMS),

                100

            )

        } else {

            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)

            Toast.makeText(this@LoginActivity, "SMS Sent", Toast.LENGTH_SHORT).show()

        }

    }
}