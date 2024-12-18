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
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "size" to size,
            "stock" to stock
        ).filterValues { it != null }
    }
}



data class Category(
    val id: String = "",
    val name: String = "",
    val description: String? = null
)

data class Brand(
    val id: String = "",
    val name: String = "",
    val description: String = ""
)

data class OrderItem(
    val productId: String = "",
    val variantId: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val productName: String = "",
    val variantDetails: Variant = Variant()
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING
)

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

enum class PaymentMethod {
    COD, BANKING, E_WALLET
}

enum class PaymentStatus {
    PENDING, PAID, FAILED
}

data class ReviewUserInfo(
    val name: String = "",
    val avatar: String? = null
)

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String? = null,
    val userInfo: ReviewUserInfo = ReviewUserInfo()
)


data class CartItem(
    val productId: String = "",
    val variantId: String = "",
    val quantity: Int = 0,
)

data class Cart(
    val userId: String = "",
    val items: List<CartItem> = emptyList()
)