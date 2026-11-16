package com.example.karflex.utils

import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

fun ImageView.loadRemoteImage(url: String) {
    val localImage = when {
        url.contains("Vario", ignoreCase = true) -> com.example.karflex.R.drawable.vehicle_vario
        url.contains("NMAX", ignoreCase = true) -> com.example.karflex.R.drawable.vehicle_nmax
        url.contains("vUYy6ktf3RYqwaQJtfpyXox5h2FhAsvf", ignoreCase = true) -> com.example.karflex.R.drawable.vehicle_avanza
        url.contains("brio", ignoreCase = true) || url.contains("All-New-brio", ignoreCase = true) -> com.example.karflex.R.drawable.vehicle_brio
        else -> null
    }
    if (localImage != null) {
        setImageResource(localImage)
        return
    }
    CoroutineScope(Dispatchers.Main).launch {
        val bitmap = withContext(Dispatchers.IO) {
            runCatching { URL(url).openStream().use(BitmapFactory::decodeStream) }.getOrNull()
        }
        bitmap?.let { setImageBitmap(it) }
    }
}
