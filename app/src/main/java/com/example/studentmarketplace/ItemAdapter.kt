package com.example.studentmarketplace.com.example.studentmarketplace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmarketplace.R
import com.example.studentmarketplace.Item

class ItemAdapter(private val items: MutableList<out Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        holder.itemImage.setImageResource(item.imageResourceId)
        holder.itemPrice.text = holder.itemView.context.getString(R.string.price_format, item.price)
        holder.itemLocation.text = item.location.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView = itemView.findViewById(R.id.item_name)
        var itemDescription: TextView = itemView.findViewById(R.id.item_description)
        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemPrice: TextView = itemView.findViewById(R.id.item_price)
        var itemLocation: TextView = itemView.findViewById(R.id.item_location)
    }
}
