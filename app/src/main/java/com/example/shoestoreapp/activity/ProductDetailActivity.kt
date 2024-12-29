package com.example.shoestoreapp.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ImageSliderAdapter
import com.example.shoestoreapp.data.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ProductDetailActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val documentId = intent.getStringExtra("DOCUMENT ID")
        readDataFromFirebase(documentId!!)

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn!!.setOnClickListener() {
            finish()
        }

        updateQuantity()
    }

    private fun readDataFromFirebase(documentId: String) {
        db.collection("products")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    updateUI(document.toObject(Product::class.java)!!)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
        ratingStar.rating = (product.averageRating ?: 0f).toFloat()

        // Cài đặt ViewPager2 với Adapter
        val adapter = ImageSliderAdapter(product.images ?: emptyList())
        viewPageSlider.adapter = adapter

        // Kết nối DotsIndicator với ViewPager2
        dotsIndicator.setViewPager2(viewPageSlider)
    }

    private fun updateQuantity() {
        val removeBtn = findViewById<Button>(R.id.removeBtn)
        val addBtn = findViewById<Button>(R.id.addBtn)
        val itemQuanET = findViewById<EditText>(R.id.itemQuanET)

        var itemQuantity = itemQuanET.text.toString().toInt()

        removeBtn!!.setOnClickListener() {
            itemQuantity -= 1
        }

        addBtn!!.setOnClickListener() {
            itemQuantity += 1
        }

        itemQuanET.setText(itemQuantity.toString())
    }
}