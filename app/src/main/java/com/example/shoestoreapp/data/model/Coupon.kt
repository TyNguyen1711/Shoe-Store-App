package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Coupon (
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val discount: String = ""
)