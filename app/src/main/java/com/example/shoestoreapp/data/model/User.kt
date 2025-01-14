package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class User (
    @DocumentId
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    var searchHistory: MutableList<String> = mutableListOf()
)

