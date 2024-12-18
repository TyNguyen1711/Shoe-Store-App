package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = ""
)