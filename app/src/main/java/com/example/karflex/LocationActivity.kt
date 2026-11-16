package com.example.karflex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_location); (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this) }
    override fun onMapReady(map: GoogleMap) { val location = LatLng(-6.201733, 106.781592); map.addMarker(MarkerOptions().position(location).title("KarFlex")); map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)) }
}
