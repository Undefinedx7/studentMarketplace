package com.example.studentmarketplace.data

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.studentmarketplace.R

class ProductAdapter (private val context : Activity, private val arrayList : ArrayList<Product>): ArrayAdapter<Product>(context, R.layout.list_item, arrayList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_item, null)

        val imageView: ImageView = view.findViewById(R.id.picture_item)
        val productName: TextView = view.findViewById(R.id.product_title)
        val procuctDesc: TextView = view.findViewById(R.id.desc)
        val productPrice: TextView = view.findViewById(R.id.productPriceItem)

        imageView.setImageResource(arrayList[position].ImageId)
        productName.text=arrayList[position].itemName
        procuctDesc.text=arrayList[position].itemDesc
        productPrice.text= arrayList[position].itemPrice.toString()

        return view
    }
}