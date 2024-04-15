package com.dhv.hoangvu.dacs3_foodorderingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhv.hoangvu.dacs3_foodorderingapp.databinding.PopulerItemBinding

class PopulerAdapter(
    private val items: List<String>,
    private val image: List<Int>,
    private val price: List<String>
) :
    RecyclerView.Adapter<PopulerAdapter.PopulerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopulerViewHolder {
        return PopulerViewHolder(
            PopulerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PopulerViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item, images, price)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PopulerViewHolder(private val binding: PopulerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.imageView2
        fun bind(item: String, images: Int, price: String) {
            binding.foodNamePopuler.text = item
            binding.pricePopuler.text = price
            imagesView.setImageResource(images)
        }


    }
}