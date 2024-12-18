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
)