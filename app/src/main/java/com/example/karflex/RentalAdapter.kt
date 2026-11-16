package com.example.karflex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.data.entities.RentalListItem
import com.example.karflex.databinding.ItemRentalBinding
import com.example.karflex.utils.loadRemoteImage

class RentalAdapter(private val onUpdate: (RentalListItem) -> Unit, private val onDelete: (RentalListItem) -> Unit) : RecyclerView.Adapter<RentalAdapter.Holder>() {
    private var items = emptyList<RentalListItem>()
    fun submitList(value: List<RentalListItem>) { items = value; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemRentalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(private val binding: ItemRentalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RentalListItem) {
            binding.modelName.text = "${item.modelName} (${item.brand})"
            binding.details.text = "Start: ${item.startDate}  •  ${item.duration} day(s)"
            binding.status.text = item.status
            binding.status.setTextColor(if (item.status == "Active") 0xff2e7d32.toInt() else 0xff757575.toInt())
            binding.vehicleImage.loadRemoteImage(item.image)
            binding.update.setOnClickListener { onUpdate(item) }
            binding.delete.setOnClickListener { onDelete(item) }
        }
    }
}
