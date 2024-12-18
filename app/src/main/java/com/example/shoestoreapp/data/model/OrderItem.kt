package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.PropertyName

data class OrderItem(
    @field:PropertyName("product_id")
    val productId: String = "",

    @field:PropertyName("variant_id")
    val variantId: String = "",

    val quantity: Int = 0,
    val price: Double = 0.0,
    val productName: String = "",
)