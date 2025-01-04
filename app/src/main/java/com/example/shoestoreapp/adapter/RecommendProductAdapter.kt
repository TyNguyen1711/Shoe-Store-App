package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Product

class RecommendProductAdapter(
    private val productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<RecommendProductAdapter.ProductViewHolder>() {

    // ViewHolder class
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarTV)
        val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTV)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommend_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Bind data to the views
        holder.fullNameTextView.text = product.name
        // Use Glide to load the image into ImageView
        Glide.with(holder.itemView.context)
            .load(product.thumbnail) // Load image URL
            .into(holder.avatarImageView)

        // Handle click event
        holder.cardView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
