package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.classes.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class GetCartRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cartRef = db.collection("carts")

    // Lấy danh sách sản phẩm trong giỏ hàng
    fun getCartItems(userId: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        cartRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cartItems = document.data ?: emptyMap()
                    val products = cartItems.map { (key, value) ->
                        val productMap = value as Map<*, *>
                        Product(
                            id = key.toInt(),
                            name = productMap["name"] as String,
                            quantity = (productMap["quantity"] as Long).toInt(),
                            price = (productMap["price"] as Int),
                            thumbnail = productMap["imageUrl"] as String
                        )
                    }
                    onSuccess(products)
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener(onFailure)
    }

    // Thêm sản phẩm vào giỏ hàng
    fun addProductToCart(userId: String, product: Product, onComplete: (Boolean) -> Unit) {
        cartRef.document(userId).update(product.id.toString(), product.toMap())
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { e ->
                // Nếu tài liệu chưa tồn tại, tạo mới
                cartRef.document(userId).set(mapOf(product.id to product.toMap()))
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
    }

    // Cập nhật số lượng sản phẩm
    fun updateProductQuantity(userId: String, productId: String, quantity: Int, onComplete: (Boolean) -> Unit) {
        if (quantity <= 0) {
            removeProductFromCart(userId, productId, onComplete)
        } else {
            cartRef.document(userId).update("$productId.quantity", quantity)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    fun removeProductFromCart(userId: String, productId: String, onComplete: (Boolean) -> Unit) {
        cartRef.document(userId).update(productId, FieldValue.delete())
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}