package com.example.shoestoreapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ImageSliderAdapter
import com.example.shoestoreapp.classes.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance().getReference("products")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        readDataFromFirebase(0)
    }

    private fun readDataFromFirebase(productId: Int) {
        database.child(productId.toString()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val product = snapshot.getValue(Product::class.java)
                product?.let {
                    Log.d("ProductData", "Name: ${it.name}, Price: ${it.price}")
                    updateUI(product)
                }
            } else {
                Log.e("ProductData", "Product with id $productId not found")
            }
        }.addOnFailureListener { error ->
            Log.e("FirebaseError", "Error: ${error.message}")
        }
    }

    private fun updateUI(product: Product) {
        val productTitleTV = findViewById<TextView>(R.id.productTitleTV)
        val priceTV = findViewById<TextView>(R.id.priceTV)
        val productDescriptionTV = findViewById<TextView>(R.id.productDescriptionTV)
        val ratingStar = findViewById<RatingBar>(R.id.ratingStar)
        val viewPageSlider = findViewById<ViewPager2>(R.id.viewPageSlider)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)

        // Cập nhật thông tin cơ bản
        productTitleTV.text = product.name
        priceTV.text = product.price?.toString() ?: "0"
        productDescriptionTV.text = product.description
        ratingStar.rating = product.averageRating ?: 0f

        // Cài đặt ViewPager2 với Adapter
        val adapter = ImageSliderAdapter(product.images ?: emptyList())
        viewPageSlider.adapter = adapter

        // Kết nối DotsIndicator với ViewPager2
        dotsIndicator.setViewPager2(viewPageSlider)
    }
}