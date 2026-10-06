package com.example.karflex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MyRentalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hanya memuat layout XML-nya saja tanpa inisialisasi komponen
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}