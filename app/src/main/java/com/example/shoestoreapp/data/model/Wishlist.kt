package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

class Wishlist (
    @DocumentId
    val userId: String = "",
    val products: List<String>,

)