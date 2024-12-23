package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Order(
    @DocumentId
    val id: String = "",

    @field:PropertyName("user_id")
    val userId: String = "",

    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING
)