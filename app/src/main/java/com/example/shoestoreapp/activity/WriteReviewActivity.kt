package com.example.shoestoreapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Comment
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.example.shoestoreapp.data.model.Review
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WriteReviewActivity : AppCompatActivity() {
    private val productRepository = ProductRepository()
    private val reviewRepository = ReviewRepository()
    private val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_write_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productId = intent.getStringExtra("productId")     // Truyền productId của sản phẩm mà người dùng mua vào đây
        val productVariantIdx = intent.getStringExtra("variant")!!.toInt()  // Truyền index trong danh sách variant của variant mà người dùng chọn vào đây

        lifecycleScope.launch {
            val productRepo = productRepository.getProduct(productId!!)
            productRepo.onSuccess { product ->
                updateUI(product, product.variants[productVariantIdx])
            }.onFailure { error ->
                Log.d("Review Error", "$error")
            }
        }
    }

    private fun updateUI(product: Product, productVariant: ProductVariant) {
        val productThumbnailIV = findViewById<ImageView>(R.id.productThumbnailIV)
        val productVariantTV = findViewById<TextView>(R.id.productVariantTV)
        val productNameTV = findViewById<TextView>(R.id.productNameTV)
        val productRatingBar = findViewById<RatingBar>(R.id.ratingBar)
        val commentET = findViewById<TextView>(R.id.commentET)
        val submitReviewBtn = findViewById<TextView>(R.id.submitReviewBtn)
        val backBtn = findViewById<Button>(R.id.reviewact_backBtn)
        val user = FirebaseAuth.getInstance().currentUser

        backBtn.setOnClickListener {
            finish()
        }

        Glide.with(productThumbnailIV.context).load(product.thumbnail).into(productThumbnailIV)

        productVariantTV.text = "Size: ${productVariant.size}"
        productNameTV.text = product.name

        submitReviewBtn.setOnClickListener {
            lifecycleScope.launch {
                val comment = Comment(user!!.displayName, user.email, productRatingBar.rating.toDouble(), commentET.text.toString())
                val reviewRepo = reviewRepository.getReview(product.id)
                reviewRepo.onSuccess { review ->
                    commentList.clear()
                    commentList.addAll(review.commentList)
                    commentList.add(comment)
                    val newReview = Review(product.id, commentList)
                    reviewRepository.updateReview(newReview)
                }.onFailure { e ->
                    Log.d("Review Error", "$e")
                }
            }
        }
    }
}


