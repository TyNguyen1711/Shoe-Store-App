package com.example.shoestoreapp.classes

data class Product(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val discountPrice: Int? = null,
    val averageRating: Float? = null,
    val reviewCount: Int? = null,
    val thumbnail: String? = null,
    val images: List<String>? = null,
    val brandId: Int? = null,
    val categoryId: Int? = null,
    val variants: List<Variant>? = null,
    var quantity: Int = 0,
    val size: Int = 0,
    var isChecked: Boolean = false
) {
    // Hàm chuyển đổi đối tượng thành Map (Firestore yêu cầu dữ liệu dạng Map)
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "quantity" to quantity,
            "price" to price,
            "size" to size,
            "images" to images,
            "isChecked" to isChecked,
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

data class Variant(
    val id: Int? = null,
    val size: String? = null,
    val stock: Int? = null
) {
    // Hàm chuyển đổi Variant thành Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "size" to size,
            "stock" to stock
        ).filterValues { it != null } // Bỏ qua các giá trị null
    }
}