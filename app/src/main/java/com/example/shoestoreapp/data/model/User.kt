package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class User (
    @DocumentId
    val id: String = "",
    val username: String = "",
    val email: String = "",
    var searchHistory: MutableList<String> = mutableListOf(),
    var bio: String ="",
    var sex: String = "",
    var birthday: String = "",
    var fullname: String = "",
    var phoneNumber: String = "",
    @get:PropertyName("isAdmin")
    @set:PropertyName("isAdmin")
    var isAdmin: Boolean = false,
    val avatar: String = ""
)
