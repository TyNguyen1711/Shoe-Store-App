package com.example.shoestoreapp.data.model

data class ProductVariant(
    val id: String = "",
    val size: String = "",
    val stock: Int = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "size" to size,
            "stock" to stock
        ).filterValues { it != null }
    }
}