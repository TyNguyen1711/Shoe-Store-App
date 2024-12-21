package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String? = null,
    val userInfo: ReviewUserInfo = ReviewUserInfo()
)