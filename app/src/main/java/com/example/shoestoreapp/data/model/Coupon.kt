package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.type.DateTime

data class Coupon (
    @DocumentId
    val id: String = "",
    val code: String = "",
    val description: String = "",
    val quantity: Int = 0,
    val discount: Int = 0,
    val threshold: Int = 0,
)