package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

class Wishlist (
    @DocumentId
    val id: String = "",
    val products: List<String>,
    val userId: String = ""
)