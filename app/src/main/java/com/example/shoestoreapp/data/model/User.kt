package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class User (
    @DocumentId
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val isAdmin: Boolean = false
)