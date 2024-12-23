package com.example.shoestoreapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        // Nhận danh sách các ID sản phẩm
        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: emptyList<String>()

        if (selectedProductIds.isNotEmpty()) {
            loadSelectedProducts(selectedProductIds)
        }
    }

    private fun loadSelectedProducts(productIds: List<String>) {
        val firestore = FirebaseFirestore.getInstance()
        val selectedProducts = mutableListOf<Product>()

        CoroutineScope(Dispatchers.IO).launch {
            for (productId in productIds) {
                val result = firestore.collection("P")
            }
        }
    }
}