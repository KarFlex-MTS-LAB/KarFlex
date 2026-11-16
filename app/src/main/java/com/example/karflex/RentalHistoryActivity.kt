package com.example.karflex

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karflex.data.entities.RentalListItem
import com.example.karflex.databinding.ActivityRentalHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class RentalHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRentalHistoryBinding
    private var allItems = emptyList<RentalListItem>()
    private val adapter = RentalAdapter({}, {})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.historyList.layoutManager = LinearLayoutManager(this)
        binding.historyList.adapter = adapter
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { adapter.submitList(allItems.filter { it.modelName.contains(s.toString(), true) }) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@RentalHistoryActivity)
            val user = withContext(Dispatchers.IO) { db.UserDao().getUserByUsername(getSharedPreferences("session", 0).getString("username", "") ?: "") } ?: return@launch
            allItems = withContext(Dispatchers.IO) { db.RentalDao().getRentalsForUser(user.UserId) }
            allItems.forEach { item ->
                if (item.status == "Active" && runCatching { LocalDate.parse(item.startDate).plusDays(item.duration.toLong()).isBefore(LocalDate.now()) }.getOrDefault(false)) withContext(Dispatchers.IO) { db.RentalDao().updateStatus(item.RentalId, "Completed") }
            }
            allItems = withContext(Dispatchers.IO) { db.RentalDao().getRentalsForUser(user.UserId) }
            adapter.submitList(allItems)
        }
    }
}
