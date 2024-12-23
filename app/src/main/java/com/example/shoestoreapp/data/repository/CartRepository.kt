package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class CartRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance())
{
    private val cartRef = db.collection("carts")

    // Lấy danh sách sản phẩm trong giỏ hàng
    suspend fun getCartItems(userId: String): Result<List<CartItem>> = runCatching {
        val document = cartRef.document(userId).collection("products").get().await()
        document.toObjects(CartItem::class.java)
    }

    // Thêm sản phẩm vào giỏ hàng
    suspend fun addProductToCart(userId: String, product: CartItem): Result<Boolean> = runCatching {
        try {
            cartRef.document(userId).update(product.productId, product.toMap()).await()
        } catch (e: Exception) {
            // Nếu tài liệu chưa tồn tại, tạo mới
            cartRef.document(userId).set(mapOf(product.productId to product.toMap())).await()
        }
        true
    }

    // Cập nhật số lượng sản phẩm
    suspend fun updateProductQuantity(userId: String, productId: String, quantity: Int): Result<Boolean> = runCatching {
        if (quantity <= 0) {
            removeProductFromCart(userId, productId)
        } else {
            cartRef.document(userId).update("$productId.quantity", quantity).await()
        }
        true
    }

    // Xóa sản phẩm khỏi giỏ hàng
    suspend fun removeProductFromCart(userId: String, productId: String): Result<Boolean> = runCatching {
        cartRef.document(userId).update(productId, FieldValue.delete()).await()
        true
    }
}
