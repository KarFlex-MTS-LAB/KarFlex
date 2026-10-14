package com.example.karflex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.example.karflex.utils.SmsUtils
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class OtpActivity : AppCompatActivity() {

    private val etDigit1 by lazy { findViewById<EditText>(R.id.etDigit1) }
    private val etDigit2 by lazy { findViewById<EditText>(R.id.etDigit2) }
    private val etDigit3 by lazy { findViewById<EditText>(R.id.etDigit3) }
    private val etDigit4 by lazy { findViewById<EditText>(R.id.etDigit4) }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        val otpCode = message?.replace(Regex("[^0-9]"), "")
                        if (!otpCode.isNullOrEmpty()) {
                            etDigit1.setText(otpCode[0].toString())
                            etDigit2.setText(otpCode[1].toString())
                            etDigit3.setText(otpCode[2].toString())
                            etDigit4.setText(otpCode[3].toString())
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // what you want to do when the timeout occurs
                        Toast.makeText(this@OtpActivity, "OTP Code Timeout", Toast.LENGTH_SHORT).show();
                        val resentTv = findViewById<TextView>(R.id.tvResend);
                        resentTv.setOnClickListener {
                            Toast.makeText(this@OtpActivity, "Mengirim ulang kode OTP...", Toast.LENGTH_SHORT).show()
                            startSmsRetriever()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val mainView = findViewById<LinearLayout>(R.id.main)
        var kodeOtpAsli = intent.getStringExtra("KODE_OTP_ASLI")
        val phoneNumber = intent.getStringExtra("NOMOR_HP");

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
        val resentCode = findViewById<TextView>(R.id.tvResend);

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
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } else {
                Toast.makeText(this, "Wrong OTP code, please try again!", Toast.LENGTH_SHORT).show()
            }
        }

        resentCode.setOnClickListener {
            Toast.makeText(this, "Resent the OTP Code...", Toast.LENGTH_SHORT).show()
            val otpAngka = Random.nextInt(1000, 9999).toString()
            SmsUtils.sentOtp(this, phoneNumber.toString(), otpAngka);
            kodeOtpAsli = otpAngka;
            startSmsRetriever()
        }

        startSmsRetriever();
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            Log.d("OTP_DEBUG", "SmsRetriever successfully started. Waiting for SMS...")
        }
        task.addOnFailureListener {
            Log.e("OTP_DEBUG", "SmsRetriever failed to run.")
            Toast.makeText(this, "Auto-read failed, please input OTP code manually", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(this, smsVerificationReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsVerificationReceiver)
    }
}