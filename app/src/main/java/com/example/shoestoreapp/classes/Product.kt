package com.example.shoestoreapp.classes

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val rating: Float,
    val soldCount: Int,
    val salePercentage: String,
    val category: String
)