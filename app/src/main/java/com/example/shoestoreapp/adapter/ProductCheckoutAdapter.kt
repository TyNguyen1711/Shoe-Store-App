package com.example.shoestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R

class ProductCheckoutAdapter(
    private var productList: List<com.example.shoestoreapp.data.model.CartItem>,
    private var images: List<String>,
    private var prices: List<Double>,
    private var names: List<String>
) : RecyclerView.Adapter<ProductCheckoutAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImageCheckoutIV)
        val productName: TextView = itemView.findViewById(R.id.nameProductCheckoutTV)
        val productSize: TextView = itemView.findViewById(R.id.sizeProductCheckoutTV)
        val productQuantity: TextView = itemView.findViewById(R.id.quantityCheckoutTV)
        val productPrice: TextView = itemView.findViewById(R.id.costProductCheckoutTV)
    }

    // Phương thức cập nhật dữ liệu cho Adapter
    fun updateData(
        newProductList: List<com.example.shoestoreapp.data.model.CartItem>,
        newImages: List<String>,
        newPrices: List<Double>,
        newNames: List<String>
    ) {
        productList = newProductList
        images = newImages
        prices = newPrices
        names = newNames
        notifyDataSetChanged() // Thông báo cho RecyclerView cập nhật dữ liệu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_in_checkout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Load ảnh sản phẩm
        Glide.with(holder.itemView.context).load(images[position]).into(holder.productImage)

        // Set thông tin sản phẩm
        holder.productName.text = names[position]
        holder.productPrice.text = "${String.format("%,.0f", prices[position])}đ"
        holder.productQuantity.text = "x${product.quantity}"
        holder.productSize.text = product.size
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
