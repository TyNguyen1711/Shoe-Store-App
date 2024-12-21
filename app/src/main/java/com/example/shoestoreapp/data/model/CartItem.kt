package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class CartItem(
    val product: Product = Product(), // Thêm giá trị mặc định
    val variant: ProductVariant = ProductVariant(), // Thêm giá trị mặc định
    var quantity: Int = 0,
    var isChecked: Boolean = false,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "product" to product.toMap(), // Chuyển đối tượng Product thành Map
            "variant" to variant.toMap(), // Chuyển đối tượng Variant thành Map
            "quantity" to quantity,
            "isChecked" to isChecked,
        ).filterValues { it != null }
    }
}
