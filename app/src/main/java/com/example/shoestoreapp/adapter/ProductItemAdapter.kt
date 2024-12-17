    package com.example.shoestoreapp.adapter

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.shoestoreapp.R
    import com.example.shoestoreapp.classes.Product

    class ProductItemAdapter(
        private val productList: List<Product>,
        private val listener: OnProductClickListener
    ) : RecyclerView.Adapter<ProductItemAdapter.ProductViewHolder>() {

        interface OnProductClickListener {
            fun onProductClick(productId: String)
        }

        class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val productImage: ImageView = view.findViewById(R.id.productImage)
            val fullNameTV: TextView = view.findViewById(R.id.fullNameTV)
            val ratingsTV: TextView = view.findViewById(R.id.ratingsTV)
            val soldTV: TextView = view.findViewById(R.id.soldTV)
            val costTV: TextView = view.findViewById(R.id.costTV)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_details, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = productList[position]

            // Load product data into views
            Glide.with(holder.productImage.context).load(product.thumbnail).into(holder.productImage)
            holder.fullNameTV.text = product.name
            holder.ratingsTV.text = "${product.averageRating} stars"
            // holder.soldTV.text = "${product.sold} sold"
            holder.costTV.text = "${product.price}đ"

            // Handle click event
            holder.itemView.setOnClickListener {
                listener.onProductClick(product.id.toString()) // Pass product ID
            }
        }

        override fun getItemCount(): Int = productList.size
    }