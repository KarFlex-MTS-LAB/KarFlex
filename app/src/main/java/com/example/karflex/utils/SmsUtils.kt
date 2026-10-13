package com.example.karflex.utils

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast

object SmsUtils {
    private val APP_SIGNATURE_HASH = "WmaimH21n/v";

    fun sentOtp(context: Context, destNumber: String, otpAngka: String) {
        try {
            val pesanOtp = "<#> Karflex OTP Code is: $otpAngka\n$APP_SIGNATURE_HASH"

            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            smsManager.sendTextMessage(destNumber, null, pesanOtp, null, null)
            Toast.makeText(context, "OTP $otpAngka has been sent to $destNumber", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed send OTP due to ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}