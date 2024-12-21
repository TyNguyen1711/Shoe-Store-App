package com.example.shoestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    private var products: List<CartItem>,
    private val userId: String,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private var images: List<String>,
    private var prices: List<Double>,
    private var names: List<String>,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTV: TextView = itemView.findViewById(R.id.fullNameTV)
        val costTV: TextView = itemView.findViewById(R.id.costTV)
        val soldTV: TextView = itemView.findViewById(R.id.soldTV)
        val increaseButton: Button = itemView.findViewById(R.id.increaseButton)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseButton)
        val sizeTV: TextView = itemView.findViewById(R.id.sizeProductTV)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)

        fun bind(product: CartItem, name: String, price: Double, thumbnail: String) {
            fullNameTV.text = name
            costTV.text = price.toString()
            soldTV.text = product.quantity.toString()
            sizeTV.text = product.size

            // Load ảnh
            Glide.with(productImage.context).load(thumbnail).into(productImage)

            // Đặt lại trạng thái CheckBox
            checkBox.setOnCheckedChangeListener(null) // Xóa listener trước khi cập nhật
            checkBox.isChecked = product.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCheckedChange(position, isChecked)
                }
            }

            // Xử lý sự kiện tăng/giảm số lượng
            increaseButton.setOnClickListener {
                notifyItemChanged(adapterPosition)
                onIncrease(product)
            }
            decreaseButton.setOnClickListener {
                if (product.quantity > 1) {
                    notifyItemChanged(adapterPosition)
                    onDecrease(product)
                } else {
                    onRemove(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_cart_details, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(products[position], names[position], prices[position], images[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateData(
        newCartItems: List<CartItem>,
        newImages: List<String> = emptyList(),
        newNames: List<String> = emptyList(),
        newPrices: List<Double> = emptyList()
    ) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = products.size

            override fun getNewListSize() = newCartItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // So sánh ID sản phẩm
                return products[oldItemPosition].productId == newCartItems[newItemPosition].productId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // So sánh nội dung sản phẩm
                val oldCartItem = products[oldItemPosition]
                val newCartItem = newCartItems[newItemPosition]

                return oldCartItem.productId == newCartItem.productId &&
                        oldCartItem.quantity == newCartItem.quantity &&
                        oldCartItem.isChecked == newCartItem.isChecked
            }
        })

        // Cập nhật danh sách sản phẩm
        products = newCartItems.toList()
        println(products)

        // Gán images, names, và prices chỉ khi có giá trị mới
        if (newImages.isNotEmpty()) {
            images = newImages
        }
        if (newNames.isNotEmpty()) {
            names = newNames
        }
        if (newPrices.isNotEmpty()) {
            prices = newPrices
        }

        // Cập nhật RecyclerView
        diffResult.dispatchUpdatesTo(this)
    }


    fun setAllChecked(isChecked: Boolean) {
        val updatedCartItems = products.map { it.copy(isChecked = isChecked) }
        updateData(updatedCartItems)
    }

    fun onIncrease(product: CartItem) {
        val updatedQuantity = product.quantity + 1  // Tăng số lượng
        // Cập nhật vào Firestore, chỉ cập nhật trường "quantity"
        FirebaseFirestore.getInstance()
            .collection("carts")
            .document(userId)
            .collection("products")
            .document(product.productId.toString())
            .update("quantity", updatedQuantity)  // Cập nhật trường quantity
            .addOnSuccessListener {
                // Cập nhật lại dữ liệu trong adapter sau khi thành công
                val updatedCartItems = products.map {
                    if (it.productId == product.productId) it.copy(quantity = updatedQuantity) else it
                }
                updateData(updatedCartItems)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error updating product: ${exception.message}")
            }
    }

    fun onDecrease(product: CartItem) {
        if (product.quantity > 1) {
            val updatedQuantity = product.quantity - 1  // Giảm số lượng
            // Cập nhật vào Firestore, chỉ cập nhật trường "quantity"
            FirebaseFirestore.getInstance()
                .collection("carts")
                .document(userId)
                .collection("products")
                .document(product.productId)
                .update("quantity", updatedQuantity)  // Cập nhật trường quantity
                .addOnSuccessListener {
                    // Cập nhật lại dữ liệu trong adapter sau khi thành công
                    val updatedCartItems = products.map {
                        if (it.productId == product.productId) it.copy(quantity = updatedQuantity) else it
                    }
                    updateData(updatedCartItems)
                }
        } else {
            // Nếu số lượng <= 1, xóa sản phẩm
            onRemove(product)
        }
    }
}

