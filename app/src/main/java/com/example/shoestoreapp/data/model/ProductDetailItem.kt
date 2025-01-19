package com.example.shoestoreapp.data.model

data class ProductDetailItem(
    val productId: String,
    val quantity: Int,
    val size: String,
    val name: String,
    val thumbnail: String,
    val price: Double
)