package com.example.shoestoreapp.data.model

data class CartItem(
    var productId: String = "", // Thêm giá trị mặc định
    val size: String = "", // Thêm giá trị mặc định
    var quantity: Int = 0,
    var isChecked: Boolean = false,
){
    constructor() : this("", "", 0, false) // Constructor không tham số

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productId" to productId, // Chuyển đối tượng Product thành Map
            "size" to size, // Chuyển đối tượng Variant thành Map
            "quantity" to quantity,
            "isChecked" to isChecked,
        ).filterValues { it != null }
    }
}
