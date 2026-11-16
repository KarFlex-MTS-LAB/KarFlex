package com.example.karflex

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.karflex.databinding.FragmentMyRentalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MyRentalFragment : Fragment() {
    private var _binding: FragmentMyRentalBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyRentalBinding.inflate(inflater, container, false)
        val adapter = RentalAdapter(::updateRental, ::deleteRental)
        binding.rentalList.layoutManager = LinearLayoutManager(requireContext())
        binding.rentalList.adapter = adapter
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val user = withContext(Dispatchers.IO) { db.UserDao().getUserByUsername(sessionUsername()) }
            if (user != null) {
                var items = withContext(Dispatchers.IO) { db.RentalDao().getRentalsForUser(user.UserId) }
                items.filter { it.status == "Active" && runCatching { LocalDate.parse(it.startDate).plusDays(it.duration.toLong()).isBefore(LocalDate.now()) }.getOrDefault(false) }
                    .forEach { withContext(Dispatchers.IO) { db.RentalDao().updateStatus(it.RentalId, "Completed") } }
                items = withContext(Dispatchers.IO) { db.RentalDao().getRentalsForUser(user.UserId) }
                adapter.submitList(items)
            }
        }
        return binding.root
    }
    private fun sessionUsername() = requireContext().getSharedPreferences("session", 0).getString("username", "") ?: ""
    private fun updateRental(item: com.example.karflex.data.entities.RentalListItem) {
        val input = EditText(requireContext()).apply { inputType = InputType.TYPE_CLASS_NUMBER }
        AlertDialog.Builder(requireContext()).setTitle("Update duration").setView(input).setPositiveButton("Save") { _, _ ->
            val value = input.text.toString().toIntOrNull()
            if (value == null || value <= 0) Toast.makeText(requireContext(), "Duration must be more than 0", Toast.LENGTH_SHORT).show()
            else lifecycleScope.launch(Dispatchers.IO) { AppDatabase.getDatabase(requireContext()).RentalDao().updateDuration(item.RentalId, value) }
        }.setNegativeButton("Cancel", null).show()
    }
    private fun deleteRental(item: com.example.karflex.data.entities.RentalListItem) = AlertDialog.Builder(requireContext()).setTitle("Cancel rental?").setMessage("This action cannot be undone.").setPositiveButton("Cancel rental") { _, _ -> lifecycleScope.launch(Dispatchers.IO) { AppDatabase.getDatabase(requireContext()).RentalDao().deleteRental(item.RentalId) } }.setNegativeButton("Keep", null).show()
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
