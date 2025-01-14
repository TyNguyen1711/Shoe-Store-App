package com.example.shoestoreapp.activity

import com.example.shoestoreapp.R
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.data.model.Product


class ProductAdapter(
    private val products: MutableList<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvDiscountPrice: TextView = itemView.findViewById(R.id.tvDiscountPrice)
        private val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvPrice.text = "$ ${String.format("%.2f", product.price)}"
            tvBrand.text = "Brand: ${product.brand}"

            // Load image using Glide
            Glide.with(itemView.context)
                .load(product.thumbnail)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivProductImage)

            // Handle discount price if available
            product.discountPrice?.let { discountPrice ->
                tvDiscountPrice.visibility = View.VISIBLE
                tvDiscountPrice.text = "$ ${String.format("%.2f", discountPrice)}"
                tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } ?: run {
                tvDiscountPrice.visibility = View.GONE
                tvPrice.paintFlags = 0
            }

            // Click listeners
            btnEdit.setOnClickListener { onEditClick(product) }
            btnDelete.setOnClickListener { onDeleteClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun removeProduct(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
    }
}