package com.example.shoestoreapp.data.model

data class Order(
    var id: String,
    val userId: String,
    val products: List<ProductItem>,
    val totalPayment: Double,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddress: String,
    val message: String,
    val paymentMethod: String,
    val orderTime: String,
    val voucher: Double,
    val status: String
)

data class ProductItem(
    val productId: String,
    val quantity: Int,
    val size: String
)