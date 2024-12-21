package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class ReviewUserInfo(
    val name: String = "",
    val avatar: String? = null
)