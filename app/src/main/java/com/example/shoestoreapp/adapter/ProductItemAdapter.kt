package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.Wishlist
import com.example.shoestoreapp.data.repository.WishListRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProductItemAdapter(
    private val productList: List<Product>,
    private val listener: OnProductClickListener,
    private val lifecycleOwner: LifecycleOwner,
    private val isHome: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_MORE_BUTTON = 1
    }

    interface OnProductClickListener {
        fun onProductClick(productId: String)
        fun onMoreButtonClick()
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position == productList.size) and isHome) VIEW_TYPE_MORE_BUTTON else VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PRODUCT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_details, parent, false)
            ProductViewHolder(view, lifecycleOwner)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.more_button, parent, false)
            MoreButtonViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            val product = productList[position]
            holder.bind(product, listener)
        } else if (holder is MoreButtonViewHolder) {
            holder.bind(listener)
        }
    }

    override fun getItemCount(): Int {
        return if(isHome) productList.size + 1 else productList.size // Thêm 1 cho thẻ "More Button"
    }

    class ProductViewHolder(itemView: View, private val lifecycleOwner: LifecycleOwner)
        : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.fullNameTV)
        private val productPrice: TextView = itemView.findViewById(R.id.costTV)
        private val productRating: TextView = itemView.findViewById(R.id.ratingsTV)
        private val productFavoriteBtn: ImageButton = itemView.findViewById(R.id.favoriteBtn)

        private lateinit var wishlists: Wishlist

        private val wishListRepository = WishListRepository()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

        fun bind(product: Product, listener: OnProductClickListener) {
            // Gắn dữ liệu sản phẩm
            Glide.with(productImage.context).load(product.thumbnail).into(productImage)
            productName.text = product.name

            // Định dạng giá
            val formattedPrice = formatCurrency(product.price.toDouble())
            productPrice.text = formattedPrice

            productRating.text = "${product.averageRating} stars"
            println("User ID: $userId")

            lifecycleOwner.lifecycleScope.launch {
                val result = wishListRepository.getUserWishlist(userId)
                result.onSuccess { items ->
                    wishlists = items
                }.onFailure { error ->
                    // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                    println("Failed to fetch wishlist of user $userId: ${error.message}")
                }
            }

            productFavoriteBtn.setOnClickListener {
                if (wishlists.products?.contains(product.id) == false) {
                    productFavoriteBtn.setImageResource(R.drawable.favorite_full)
                    wishlists.products!!.add(product.id)
                    updateUserWishlist(wishlists)
                }
                else {
                    productFavoriteBtn.setImageResource(R.drawable.favorite_border)
                    wishlists.products!!.remove(product.id)
                    updateUserWishlist(wishlists)
                }
            }

            itemView.setOnClickListener {
                listener.onProductClick(product.id.toString())
            }
        }

        private fun formatCurrency(amount: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            return formatter.format(amount)
        }

        private fun updateUserWishlist(wishlist: Wishlist) {
            lifecycleOwner.lifecycleScope.launch {
                val result = wishListRepository.updateUserWishlist(wishlist)
            }
        }
    }

    class MoreButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moreButton: ImageButton = itemView.findViewById(R.id.moreBtn)

        fun bind(listener: OnProductClickListener) {
            moreButton.setOnClickListener {
                listener.onMoreButtonClick()
            }
        }
    }
}