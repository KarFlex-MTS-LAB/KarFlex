package com.example.karflex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
class OtpActivity : AppCompatActivity() {

    private lateinit var etOtp: EditText

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
                            etOtp.setText(otpCode)
                        }

                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
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

        btnReturn.setOnClickListener {
            finish()
        }

        btnVerify.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        startSmsRetriever();
    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            Log.d("OTP_DEBUG", "SmsRetriever berhasil dijalankan. Menunggu SMS...")
        }
        task.addOnFailureListener {
            Log.e("OTP_DEBUG", "SmsRetriever gagal dijalankan.")
            Toast.makeText(this, "Auto-baca SMS gagal, silakan ketik OTP secara manual", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsVerificationReceiver)
    }
}