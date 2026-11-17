package com.example.karflex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages: Array<SmsMessage>? = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages != null) {
                for (sms in messages) {
                    val messageBody = sms.messageBody ?: continue
                    val otpRegex = Regex("""\b\d{4}\b""")
                    val matchResult = otpRegex.find(messageBody)
                    if (matchResult != null) {
                        val otpCode = matchResult.value
                        onOtpReceivedListener?.invoke(otpCode)
                        break
                    }
                }
            }
        }
    }

    companion object {
        var onOtpReceivedListener: ((String) -> Unit)? = null
    }
}
