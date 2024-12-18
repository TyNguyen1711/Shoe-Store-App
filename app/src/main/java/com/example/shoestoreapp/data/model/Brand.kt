package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Brand (
    @DocumentId
    val id: String = "",
    val name: String = ""
)