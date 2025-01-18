package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val productId: String? = "",
    val commentList: List<Comment> = emptyList()
)