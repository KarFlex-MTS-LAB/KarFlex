package com.example.karflex

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class OtpActivity : AppCompatActivity() {

    private val etDigit1 by lazy { findViewById<EditText>(R.id.etDigit1) }
    private val etDigit2 by lazy { findViewById<EditText>(R.id.etDigit2) }
    private val etDigit3 by lazy { findViewById<EditText>(R.id.etDigit3) }
    private val etDigit4 by lazy { findViewById<EditText>(R.id.etDigit4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val mainView = findViewById<LinearLayout>(R.id.main)
        var kodeOtpAsli = intent.getStringExtra("KODE_OTP_ASLI")
        val phoneNumber = intent.getStringExtra("NOMOR_HP")
        val username = intent.getStringExtra("USERNAME")

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
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val resentCode = findViewById<TextView>(R.id.tvResend)

        btnReturn.setOnClickListener {
            finish()
        }

        btnVerify.setOnClickListener {
            val inputUser = etDigit1.text.toString() +
                    etDigit2.text.toString() +
                    etDigit3.text.toString() +
                    etDigit4.text.toString()

            if (inputUser.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputUser == kodeOtpAsli) {
                Toast.makeText(this, "Successfully Verified", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)

                intent.putExtra("USERNAME", username)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } else {
                Toast.makeText(this, "Wrong OTP code, please try again!", Toast.LENGTH_SHORT).show()
            }
        }

        resentCode.setOnClickListener {
            Toast.makeText(this, "Resent the OTP Code...", Toast.LENGTH_SHORT).show()
            val otpAngka = Random.nextInt(1000, 9999).toString()
            checkSendSMSPermission(phoneNumber, otpAngka);
            kodeOtpAsli = otpAngka;
        }
    }

    fun checkSendSMSPermission(phone: String?, message: String?) {

        if (ActivityCompat.checkSelfPermission(

                this@OtpActivity,

                android.Manifest.permission.SEND_SMS

            ) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(

                this@OtpActivity,

                arrayOf<String>(android.Manifest.permission.SEND_SMS),

                100

            )

        } else {

            val smsManager = SmsManager.getDefault()

            smsManager.sendTextMessage(phone, null, message, null, null)

            Toast.makeText(this@OtpActivity, "SMS Sent", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
