    package com.example.shoestoreapp.adapter

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.shoestoreapp.R
    import com.example.shoestoreapp.classes.Product

    class ProductItemAdapter(
        private val productList: List<Product>,
        private val listener: OnProductClickListener
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
            return if (position == productList.size) VIEW_TYPE_MORE_BUTTON else VIEW_TYPE_PRODUCT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_PRODUCT) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.product_details, parent, false)
                ProductViewHolder(view)
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
            return productList.size + 1 // Thêm 1 cho thẻ "More Button"
        }

        class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val productImage: ImageView = itemView.findViewById(R.id.productImage)
            private val productName: TextView = itemView.findViewById(R.id.fullNameTV)
            private val productPrice: TextView = itemView.findViewById(R.id.costTV)

            fun bind(product: Product, listener: OnProductClickListener) {
                // Gắn dữ liệu sản phẩm
                Glide.with(productImage.context).load(product.thumbnail).into(productImage)
                productName.text = product.name
                productPrice.text = "${product.price}đ"

                itemView.setOnClickListener {
                    listener.onProductClick(product.id.toString())
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

