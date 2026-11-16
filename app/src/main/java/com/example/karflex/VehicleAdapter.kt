package com.example.karflex

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.data.entities.VehicleCard
import com.example.karflex.databinding.ItemVehicleBinding
import com.example.karflex.utils.loadRemoteImage
import java.text.NumberFormat
import java.util.Locale

class VehicleAdapter(private val onClick: (VehicleCard) -> Unit) : RecyclerView.Adapter<VehicleAdapter.Holder>() {
    private var items = emptyList<VehicleCard>()
    fun submitList(value: List<VehicleCard>) { items = value; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(private val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VehicleCard) {
            binding.modelName.text = item.modelName
            binding.brand.text = item.brand
            binding.price.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(item.pricePerDay) + "/day"
            binding.vehicleImage.loadRemoteImage(item.image)
            binding.root.setCardBackgroundColor(if (item.status == "Active") Color.rgb(255, 220, 220) else Color.WHITE)
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
