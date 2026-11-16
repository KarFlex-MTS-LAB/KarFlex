package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.karflex.databinding.FragmentProfileBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val username = requireContext().getSharedPreferences("session", 0).getString("username", "User")
        binding.profileTitle.text = username
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { AppDatabase.getDatabase(requireContext()).UserDao().getUserByUsername(username ?: "") }
            binding.profilePhone.text = user?.phoneNumber ?: ""
        }
        binding.btnStoreLocation.setOnClickListener { startActivity(Intent(requireContext(), LocationActivity::class.java)) }
        binding.btnRentalHistory.setOnClickListener { startActivity(Intent(requireContext(), RentalHistoryActivity::class.java)) }
        binding.btnLogout.setOnClickListener { requireContext().getSharedPreferences("session", 0).edit().clear().apply(); startActivity(Intent(requireContext(), WelcomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)) }
        return binding.root
    }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
