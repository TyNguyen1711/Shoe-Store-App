package com.example.shoestoreapp.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.ProductTemp
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.BrandRepository


//class WishlistAdapter(
//    private val context: Context,
//    private var productList: List<Product>,
//    private val productCountListener: ProductCountListener? = null,
//) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {
//    interface ProductCountListener {
//        fun onProductCountChanged(count: Int)
//    }
//    private val originalProductList = productList.toList()
//    private var currentCategory: String = "All"
//    private var currentSearchQuery: String = ""
//    private val brandRepository = BrandRepository()
//    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        val heartBtn: ImageButton = itemView.findViewById(R.id.heartBtn)
////        val cartBtn: ImageButton = itemView.findViewById(R.id.cartBtn)
//        val productImage: ImageView = itemView.findViewById(R.id.productImage)
//        val fullNameTV: TextView = itemView.findViewById(R.id.fullNameTV)
//        val ratingsTV: TextView = itemView.findViewById(R.id.ratingsTV)
//        val soldTV: TextView = itemView.findViewById(R.id.soldTV)
//        val costTV: TextView = itemView.findViewById(R.id.costTV)
//        val salePercentageTV: TextView = itemView.findViewById(R.id.salePercentage)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.wishlist_product, parent, false)
//        return WishlistViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
//        val product = productList[position]
//        Glide.with(context)
//            .load(product.thumbnail)
//            .into(holder.productImage)
//
//        holder.fullNameTV.text = product.name
//        holder.ratingsTV.text = "⭐ ${product.averageRating}"
//        holder.soldTV.text = "${product.soldCount} sold"
//        holder.costTV.text = "${String.format("%,.0f", product.price)}đ"
//        holder.salePercentageTV.text = "sale ${product.salePercentage}%"
//    }
//
//    override fun getItemCount(): Int = productList.size
//
//    fun filterProducts(category: String) {
//        currentCategory = category
//        applyFilters()
//    }
//
//    fun filterProductsBySearch(query: String) {
//        currentSearchQuery = query.lowercase()
//        applyFilters()
//    }
//
//    private fun applyFilters() {
//        productList = originalProductList.filter { product ->
//            val matchesCategory = currentCategory == "All" || product.brand == currentCategory
//            val matchesSearch = currentSearchQuery.isEmpty() ||
//                    product.name.lowercase().contains(currentSearchQuery)
//
//            matchesCategory && matchesSearch
//        }
//
//        productCountListener?.onProductCountChanged(productList.size)
//        notifyDataSetChanged()
//    }
//}





class WishlistAdapter(
    private val context: Context,
    products: List<Product>,
    private val productCountListener: ProductCountListener? = null,
    private val onHeartClickListener: OnHeartClickListener? = null,
    private val onCartClickListener: OnCartClickListener? = null
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {
    private val lock = Any()
    interface ProductCountListener {
        fun onProductCountChanged(count: Int)
    }
    interface OnHeartClickListener {
        fun onHeartClick(product: Product, position: Int)
    }

    interface OnCartClickListener {
        fun onCartClick(product: Product)
    }
    private var originalProductList = products.toMutableList()
    private var productList = products.toMutableList()
    private var currentCategory: String = "All"
    private var currentSearchQuery: String = ""

    // ViewHolder class định nghĩa và ánh xạ các view trong item layout
    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val fullNameTV: TextView = itemView.findViewById(R.id.fullNameTV)
        val ratingsTV: TextView = itemView.findViewById(R.id.ratingsTV)
        val soldTV: TextView = itemView.findViewById(R.id.soldTV)
        val costTV: TextView = itemView.findViewById(R.id.costTV)
        val salePercentageTV: TextView = itemView.findViewById(R.id.salePercentage)
        val heartBtn: ImageButton = itemView.findViewById(R.id.heartBtn)
        val cartBtn: ImageButton = itemView.findViewById(R.id.cartBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.wishlist_product, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val product = productList[position]

        // Hiển thị hình ảnh sản phẩm sử dụng Glide
        Glide.with(context)
            .load(product.thumbnail)
            .into(holder.productImage)

        // Gán dữ liệu cho các TextView
        holder.fullNameTV.text = product.name
        holder.ratingsTV.text = "⭐ ${product.averageRating}"
        holder.soldTV.text = "${product.soldCount} sold"
        holder.costTV.text = "${String.format("%,.0f", product.price)}đ"
        holder.salePercentageTV.text = "sale ${product.salePercentage}%"
        holder.heartBtn.setOnClickListener {
            onHeartClickListener?.onHeartClick(product, position)
        }

        holder.cartBtn.setOnClickListener {
            onCartClickListener?.onCartClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Hàm cập nhật dữ liệu mới
    fun updateData(newProducts: List<Product>) {
        originalProductList = newProducts.toMutableList()
        applyFilters()
        notifyDataSetChanged()
    }

    // Hàm lọc theo danh mục
    fun filterProducts(category: String) {
        currentCategory = category
        applyFilters()
    }

    // Hàm lọc theo từ khóa tìm kiếm
    fun filterProductsBySearch(query: String) {
        currentSearchQuery = query.lowercase()
        applyFilters()
    }
    fun removeItem(position: Int): Product? {
        synchronized(lock) {
            if (position < 0 || position >= productList.size) return null

            val product = productList[position]
            val indexInOriginal = originalProductList.indexOf(product)

            if (indexInOriginal != -1) {
                originalProductList.removeAt(indexInOriginal)
            }
            productList.removeAt(position)

            Handler(Looper.getMainLooper()).post {
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, productList.size)
            }

            return product
        }
    }


    fun undoRemove(position: Int, product: Product) {
        synchronized(lock) {
            if (position > productList.size) return

            productList.add(position, product)
            originalProductList.add(position, product)

            Handler(Looper.getMainLooper()).post {
                notifyItemInserted(position)
                notifyItemRangeChanged(position, productList.size)
            }
        }
    }

    private fun applyFilters() {
        productList = originalProductList.filter { product ->
            val matchesCategory = currentCategory == "All" || product.brand == currentCategory
            val matchesSearch = currentSearchQuery.isEmpty() ||
                    product.name.lowercase().contains(currentSearchQuery)

            matchesCategory && matchesSearch
        }.toMutableList()

        productCountListener?.onProductCountChanged(productList.size)
        notifyDataSetChanged()
    }
}