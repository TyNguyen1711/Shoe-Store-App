package com.example.shoestoreapp.classes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R


class WishlistAdapter(
    private val context: Context,
    private var productList: List<ProductTemp>,
    private val productCountListener: ProductCountListener? = null
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {
    interface ProductCountListener {
        fun onProductCountChanged(count: Int)
    }
    private val originalProductList = productList.toList()
    private var currentCategory: String = "All"
    private var currentSearchQuery: String = ""

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val heartBtn: ImageButton = itemView.findViewById(R.id.heartBtn)
//        val cartBtn: ImageButton = itemView.findViewById(R.id.cartBtn)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val fullNameTV: TextView = itemView.findViewById(R.id.fullNameTV)
        val ratingsTV: TextView = itemView.findViewById(R.id.ratingsTV)
        val soldTV: TextView = itemView.findViewById(R.id.soldTV)
        val costTV: TextView = itemView.findViewById(R.id.costTV)
        val salePercentageTV: TextView = itemView.findViewById(R.id.salePercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.wishlist_product, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val product = productList[position]
        Glide.with(context)
            .load(product.imageUrl)
            .into(holder.productImage)

        holder.fullNameTV.text = product.name
        holder.ratingsTV.text = "${product.rating} stars"
        holder.soldTV.text = "${product.soldCount} sold"
        holder.costTV.text = "${String.format("%,.0f", product.price)}đ"
        holder.salePercentageTV.text = "sale ${product.salePercentage}"
    }

    override fun getItemCount(): Int = productList.size

    fun filterProducts(category: String) {
        currentCategory = category
        applyFilters()
    }

    fun filterProductsBySearch(query: String) {
        currentSearchQuery = query.lowercase()
        applyFilters()
    }

    private fun applyFilters() {
        productList = originalProductList.filter { product ->
            val matchesCategory = currentCategory == "All" || product.category == currentCategory
            val matchesSearch = currentSearchQuery.isEmpty() ||
                    product.name.lowercase().contains(currentSearchQuery)

            matchesCategory && matchesSearch
        }

        productCountListener?.onProductCountChanged(productList.size)
        notifyDataSetChanged()
    }
}