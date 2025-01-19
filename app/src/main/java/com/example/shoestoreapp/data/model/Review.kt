package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Review(
    @DocumentId
    val productId: String? = "",
    val commentList: List<Comment> = emptyList()
)