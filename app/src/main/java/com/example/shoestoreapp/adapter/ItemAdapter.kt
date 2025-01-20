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
import com.example.shoestoreapp.data.model.OrderMainMain
import com.example.shoestoreapp.data.model.ProductItem
import com.example.shoestoreapp.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class ItemAdapter(private val items: List<ProductItem>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    val productRepository = ProductRepository()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProduct: ImageView = view.findViewById(R.id.imageProduct)
        val nameProduct: TextView = view.findViewById(R.id.nameProduct)
        val sizeProduct: TextView = view.findViewById(R.id.sizeProduct)
        val priceProduct: TextView = view.findViewById(R.id.priceProduct)
        val quantityProduct: TextView = view.findViewById(R.id.quantityProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivery_item_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        // Hiển thị dữ liệu cơ bản trước (ví dụ như size và quantity)
        holder.sizeProduct.text = "Size: ${item.size}"
        holder.quantityProduct.text = "x${item.quantity}"

        // Sử dụng CoroutineScope để gọi getProduct
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("id",item.productId)
            val result = productRepository.getProduct(item.productId)
            if (result.isSuccess) {
                val product = result.getOrNull()
                product?.let {
                    val formatter = DecimalFormat("#,###")
                    holder.priceProduct.text = "₫${formatter.format(it.price.toInt())}"
                    holder.nameProduct.text = it.name
                    // Sử dụng Glide hoặc Picasso để tải hình ảnh
                    Glide.with(holder.itemView.context)
                        .load(it.thumbnail)
                        .into(holder.imageProduct)
                }
            } else {
                holder.nameProduct.text = "Error loading product"
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
