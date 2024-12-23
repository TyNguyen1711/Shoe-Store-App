package com.example.shoestoreapp.data.model

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

enum class PaymentMethod {
    COD, BANKING, E_WALLET
}

enum class PaymentStatus {
    PENDING, PAID, FAILED
}