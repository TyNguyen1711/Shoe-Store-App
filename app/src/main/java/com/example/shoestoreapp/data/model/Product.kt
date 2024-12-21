package com.example.shoestoreapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val description: String = "",

    @field:PropertyName("category_id")
    val categoryId: String = "",

    @field:PropertyName("brand_id")
    val brandId: String = "",

    val price: Double = 0.0,
    val discountPrice: Double? = null,
    val images: List<String> = emptyList(),
    val variants: List<ProductVariant> = emptyList(),
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0
) {
    // Hàm chuyển đổi đối tượng thành Map (Firestore yêu cầu dữ liệu dạng Map)
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "price" to price,
            "images" to images,
            "discountPrice" to discountPrice,
            "averageRating" to averageRating,
            "reviewCount" to reviewCount,
            "thumbnail" to thumbnail,
            "brandId" to brandId,
            "categoryId" to categoryId,
            "variants" to variants?.map { it.toMap() } // Chuyển danh sách variant thành map
        ).filterValues { it != null } // Bỏ qua các giá trị null
    }
}