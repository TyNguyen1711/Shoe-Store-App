package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Cart(
    val userId: String = "",
    val items: List<CartItem> = emptyList()
)