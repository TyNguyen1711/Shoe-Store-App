package com.example.shoestoreapp.adapter

import android.graphics.Color
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
import com.example.shoestoreapp.adapter.ProductItemAdapter.OnProductClickListener
import com.example.shoestoreapp.data.model.CartItem

class CartAdapter(
    private var products: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private var images: List<String>,
    private var prices: List<Double>,
    private var names: List<String>,
    private var stockList: List<Int>,
    private val onItemClick: (productId: String) -> Unit
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
            costTV.text = "${String.format("%,.0f",price)}đ"
            soldTV.text = product.quantity.toString()
            sizeTV.text = product.size

            // Load ảnh
            Glide.with(productImage.context).load(thumbnail).into(productImage)

            // Đặt lại trạng thái CheckBox
            checkBox.setOnCheckedChangeListener(null) // Xóa listener trước khi cập nhật
            checkBox.isChecked = product.isChecked
            var pos = 0
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                pos = position
                if (position != RecyclerView.NO_POSITION) {
                    onCheckedChange(position, isChecked)
                }
            }

            // Xử lý sự kiện tăng/giảm số lượng
            increaseButton.setOnClickListener {
                // Kiểm tra xem số lượng sản phẩm đã đạt tối đa chưa
                if (product.quantity < stockList[pos]) {
                    // Nếu chưa đạt tối đa, thực hiện tăng
                    notifyItemChanged(adapterPosition)
                    onIncrease(product)

                    // Nếu số lượng bằng với stockList, vô hiệu hóa nút tăng
                    if (product.quantity == stockList[pos]) {
                        increaseButton.isEnabled = false
                    }
                } else {
                    // Nếu đã đạt tối đa, không làm gì cả
                    increaseButton.isEnabled = false
                }
            }



            decreaseButton.setOnClickListener {
                notifyItemChanged(adapterPosition)
                increaseButton.isEnabled = true
                onDecrease(product)
                if (product.quantity <= 1) {
                    var mutableList = images.toMutableList() // Chuyển thành MutableList
                    mutableList.removeAt(pos) // Xóa phần tử tại vị trí idx
                    mutableList = names.toMutableList() // Chuyển thành MutableList
                    mutableList.removeAt(pos)
                    val pricesMutableList: MutableList<Double> = prices.toMutableList()
                    pricesMutableList.removeAt(pos)
                    val stockMutableList: MutableList<Int> = stockList.toMutableList()
                    stockMutableList.removeAt(pos)
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
        holder.itemView.setOnClickListener {
            onItemClick(products[position].productId) // Gọi callback khi bấm vào item
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(
        newCartItems: List<CartItem>,
        newImages: List<String> = emptyList(),
        newNames: List<String> = emptyList(),
        newPrices: List<Double> = emptyList(),
        newStock: List<Int> = emptyList()
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
        if (newStock.isNotEmpty()) {
            stockList = newStock
        }
        // Cập nhật RecyclerView
        diffResult.dispatchUpdatesTo(this)
    }


    fun setAllChecked(isChecked: Boolean) {
        val updatedCartItems = products.map { it.copy(isChecked = isChecked) }
        updateData(updatedCartItems)
    }

    fun getCheckedProductIds(): List<String> {
        return products.filter { it.isChecked }.map { it.productId }
    }


}

