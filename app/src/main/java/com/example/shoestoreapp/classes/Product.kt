package com.example.shoestoreapp.classes

data class Product(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Float? = null,
    val discountPrice: Int? = null,
    val averageRating: Float? = null,
    val reviewCount: Int? = null,
    val thumbnail: String? = null,
    val images: List<String>? = null,
    val brandId: Int? = null,
    val categoryId: Int? = null,
    val variants: List<Variant>? = null
)

data class Variant(
    val id: Int? = null,
    val size: String? = null,
    val stock: Int? = null
)