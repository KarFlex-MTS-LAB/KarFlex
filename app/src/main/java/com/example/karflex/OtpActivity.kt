package com.example.karflex

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import kotlin.random.Random

class OtpActivity : AppCompatActivity() {

    private val etDigit1 by lazy { findViewById<EditText>(R.id.etDigit1) }
    private val etDigit2 by lazy { findViewById<EditText>(R.id.etDigit2) }
    private val etDigit3 by lazy { findViewById<EditText>(R.id.etDigit3) }
    private val etDigit4 by lazy { findViewById<EditText>(R.id.etDigit4) }

    private val smsReceiver = SmsReceiver()
    private var isReceiverRegistered = false

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

        setupOtpInputNavigation()
        requestSmsPermissions()

        val btnReturn = findViewById<ImageButton>(R.id.btnReturn)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val resentCode = findViewById<TextView>(R.id.tvResend)

        btnReturn.setOnClickListener {
            finish()
        }

        btnVerify.setOnClickListener {
            val inputUser = etDigit1.text.toString().trim() +
                    etDigit2.text.toString().trim() +
                    etDigit3.text.toString().trim() +
                    etDigit4.text.toString().trim()

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
            checkSendSMSPermission(phoneNumber, otpAngka)
            kodeOtpAsli = otpAngka
        }
    }

    private fun setupOtpInputNavigation() {
        val editTexts = listOf(etDigit1, etDigit2, etDigit3, etDigit4)

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editTexts[i].text.isEmpty() && i > 0) {
                        editTexts[i - 1].requestFocus()
                        editTexts[i - 1].text?.clear()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    fun fillOtpFields(otpCode: String) {
        if (otpCode.length >= 4) {
            etDigit1.setText(otpCode[0].toString())
            etDigit2.setText(otpCode[1].toString())
            etDigit3.setText(otpCode[2].toString())
            etDigit4.setText(otpCode[3].toString())

            etDigit4.requestFocus()
            etDigit4.setSelection(etDigit4.text.length)

            Toast.makeText(this, "OTP Auto-filled: $otpCode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSmsPermissions() {
        val permissions = mutableListOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 101)
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
                arrayOf(android.Manifest.permission.SEND_SMS),
                100
            )
        } else {
            @Suppress("DEPRECATION")
            val smsManager = android.telephony.SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)
            Toast.makeText(this@OtpActivity, "SMS Sent", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        SmsReceiver.onOtpReceivedListener = { otpCode ->
            runOnUiThread {
                fillOtpFields(otpCode)
            }
        }

        if (!isReceiverRegistered) {
            val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            filter.priority = 999
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(smsReceiver, filter, RECEIVER_EXPORTED)
            } else {
                registerReceiver(smsReceiver, filter)
            }
            isReceiverRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()
        SmsReceiver.onOtpReceivedListener = null
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(smsReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isReceiverRegistered = false
        }
    }
}
