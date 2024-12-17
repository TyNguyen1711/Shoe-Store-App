package com.example.project

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
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    private var products: List<Product>,
    private val userId: String,
    private val onIncrease: (Product) -> Unit,
    private val onDecrease: (Product) -> Unit,
    private val onRemove: (Product) -> Unit,
    private val onCheckedChange: (Int, Boolean) -> Unit
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

        fun bind(product: Product) {
            fullNameTV.text = product.name
            costTV.text = "${product.price}đ"
            soldTV.text = product.quantity.toString()
            sizeTV.text = product.size.toString()

            // Load ảnh
            Glide.with(productImage.context).load(product.imageUrl).into(productImage)

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
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = products.size

            override fun getNewListSize() = newProducts.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // So sánh ID sản phẩm
                return products[oldItemPosition].id == newProducts[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // So sánh nội dung sản phẩm
                val oldProduct = products[oldItemPosition]
                val newProduct = newProducts[newItemPosition]

                return  oldProduct.name == newProduct.name &&
                        oldProduct.price == newProduct.price &&
                        oldProduct.quantity == newProduct.quantity &&
                        oldProduct.isChecked == newProduct.isChecked
            }
        })

        products = newProducts.toList() // Cập nhật danh sách sản phẩm
        diffResult.dispatchUpdatesTo(this) // Cập nhật RecyclerView
    }

    fun setAllChecked(isChecked: Boolean) {
        val updatedProducts = products.map { it.copy(isChecked = isChecked) }
        updateData(updatedProducts)
    }

    fun onIncrease(product: Product) {
        val updatedQuantity = product.quantity + 1  // Tăng số lượng
        // Cập nhật vào Firestore, chỉ cập nhật trường "quantity"
        FirebaseFirestore.getInstance()
            .collection("carts")
            .document(userId)
            .collection("products")
            .document(product.id)
            .update("quantity", updatedQuantity)  // Cập nhật trường quantity
            .addOnSuccessListener {
                // Cập nhật lại dữ liệu trong adapter sau khi thành công
                val updatedProducts = products.map {
                    if (it.id == product.id) it.copy(quantity = updatedQuantity) else it
                }
                updateData(updatedProducts)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error updating product: ${exception.message}")
            }
    }

    fun onDecrease(product: Product) {
        if (product.quantity > 1) {
            val updatedQuantity = product.quantity - 1  // Giảm số lượng
            // Cập nhật vào Firestore, chỉ cập nhật trường "quantity"
            FirebaseFirestore.getInstance()
                .collection("carts")
                .document(userId)
                .collection("products")
                .document(product.id)
                .update("quantity", updatedQuantity)  // Cập nhật trường quantity
                .addOnSuccessListener {
                    // Cập nhật lại dữ liệu trong adapter sau khi thành công
                    val updatedProducts = products.map {
                        if (it.id == product.id) it.copy(quantity = updatedQuantity) else it
                    }
                    updateData(updatedProducts)
                }
        } else {
            // Nếu số lượng <= 1, xóa sản phẩm
            onRemove(product)
        }
    }
}

