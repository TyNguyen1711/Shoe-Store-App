package com.example.shoestoreapp.data.model

data class Order(
    var id: String,
    var userId: String,
    val products: List<ProductItem>,
    val totalPayment: Double,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddress: String,
    val message: String,
    val paymentMethod: String,
    val orderTime: String,
    val status: String,
    val voucher: Double
){
    constructor() : this(
        id = "",
        userId = "",
        products = emptyList(),
        totalPayment = 0.0,
        recipientName = "",
        recipientPhone = "",
        recipientAddress = "",
        message = "",
        paymentMethod = "",
        orderTime = "",
        status = "",
        voucher = 0.0
    )
}

data class ProductItem(
    var productId: String = "",
    var quantity: Int = 0,
    var size: String = ""
) {
    // Constructor không tham số (Firebase yêu cầu)
    constructor() : this("", 0, "")
}

