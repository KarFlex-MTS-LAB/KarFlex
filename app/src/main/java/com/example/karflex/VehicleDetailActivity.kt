package com.example.karflex

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.karflex.data.entities.Rental
import com.example.karflex.databinding.ActivityVehicleDetailBinding
import com.example.karflex.utils.loadRemoteImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class VehicleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getIntExtra("VEHICLE_ID", 0)
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@VehicleDetailActivity)
            val detail = withContext(Dispatchers.IO) { db.VehicleDao().getVehicleById(id) } ?: return@launch
            binding.modelName.text = detail.modelName
            binding.description.text = detail.description
            binding.specs.text = "${detail.brand} • ${detail.type} • ${detail.transmission}\nIDR ${detail.pricePerDay}/day"
            binding.vehicleImage.loadRemoteImage(detail.image)
            if (detail.status == "Active") { binding.duration.visibility = android.view.View.GONE; binding.rent.visibility = android.view.View.GONE; binding.rentedNotice.visibility = android.view.View.VISIBLE }
            binding.rent.setOnClickListener {
                val duration = binding.duration.text.toString().toIntOrNull()
                if (duration == null || duration <= 0) { Toast.makeText(this@VehicleDetailActivity, "Duration must be more than 0", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                lifecycleScope.launch(Dispatchers.IO) {
                    val username = getSharedPreferences("session", 0).getString("username", "") ?: ""
                    val user = db.UserDao().getUserByUsername(username) ?: return@launch
                    db.RentalDao().insertARental(Rental(UserId = user.UserId, VehicleId = id, duration = duration, startDate = LocalDate.now().toString(), status = "Active"))
                    launch(Dispatchers.Main) { Toast.makeText(this@VehicleDetailActivity, "Rental saved", Toast.LENGTH_SHORT).show(); finish() }
                }
            }
        }
    }
}
