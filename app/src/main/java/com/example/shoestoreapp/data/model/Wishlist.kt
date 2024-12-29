package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Wishlist (
    @DocumentId
    val userId: String = "",
    val products: MutableList<String>?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "products" to products
        ).filterValues { it != null }
    }
}