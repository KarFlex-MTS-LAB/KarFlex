package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karflex.data.repository.VehicleRepository
import com.example.karflex.databinding.FragmentBrowseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseFragment : Fragment() {
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        val adapter = VehicleAdapter { item -> startActivity(Intent(requireContext(), VehicleDetailActivity::class.java).putExtra("VEHICLE_ID", item.VehicleId)) }
        binding.vehicleList.layoutManager = LinearLayoutManager(requireContext())
        binding.vehicleList.adapter = adapter
        lifecycleScope.launch {
            val repo = VehicleRepository(requireContext())
            withContext(Dispatchers.IO) { repo.initialVehicle() }
            adapter.submitList(withContext(Dispatchers.IO) { repo.getVehicle() })
        }
        return binding.root
    }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
