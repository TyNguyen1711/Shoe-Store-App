package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId

data class Wishlist (
    @DocumentId
    var userId: String = "",
    var products: MutableList<String>?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "products" to products
        ).filterValues { it != null }
    }
}