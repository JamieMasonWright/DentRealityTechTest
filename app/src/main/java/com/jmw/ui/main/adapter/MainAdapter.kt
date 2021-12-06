package com.jmw.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmw.R
import com.jmw.data.model.Country
import kotlinx.android.synthetic.main.item_layout.view.*

class MainAdapter(
    private val countries: ArrayList<Country>
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(country: Country) {
            itemView.textViewUserName.text = country.name
            itemView.textViewUserEmail.text = country.country_code
            //Could add a flag type avatar in here using Glide. Use PNG images and then convert using Bitmap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = countries.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(countries[position])

    fun addData(list: List<Country>) {
        countries.addAll(list)
    }

}