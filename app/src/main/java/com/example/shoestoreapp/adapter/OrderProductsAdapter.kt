package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.ProductDetailItem
import java.text.NumberFormat
import java.util.Locale

class OrderProductsAdapter : ListAdapter<ProductDetailItem, OrderProductsAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivProductThumbnail)
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val  tvSize: TextView = itemView.findViewById(R.id.tvProductSize)
        fun bind(product: ProductDetailItem) {
            Glide.with(itemView.context)
                .load(product.thumbnail)
                .placeholder(R.drawable.placeholder_image) // Add a placeholder image
                .into(ivThumbnail)

            tvName.text = product.name
            tvQuantity.text = "Số lượng: ${product.quantity}"
            tvPrice.text = formatCurrency(product.price)
            tvSize.text = "Kích cỡ: ${product.size}"
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            return format.format(amount)
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductDetailItem>() {
        override fun areItemsTheSame(oldItem: ProductDetailItem, newItem: ProductDetailItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductDetailItem, newItem: ProductDetailItem): Boolean {
            return oldItem == newItem
        }
    }
}