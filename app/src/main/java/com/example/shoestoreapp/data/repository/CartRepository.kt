package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
            // Truy cập vào 'products' của userId và cập nhật productId
            cartRef.document(userId)
                .collection("products")
                .document(product.productId)
                .set(product.toMap(), SetOptions.merge()) // Merge dữ liệu nếu đã tồn tại
                .await()
        } catch (e: Exception) {
            throw e // Ném lỗi ra để Result ghi nhận
        }
        true
    }


    // Cập nhật số lượng sản phẩm
    suspend fun updateProductQuantity(userId: String, productId: String, quantity: Int): Result<Pair<Boolean, Int>> = runCatching {
        val productsSnapshot = cartRef.document(userId).collection("products").get().await()

        // Tìm vị trí của document
        val productList = productsSnapshot.documents
        val index = productList.indexOfFirst { it.id == productId }

        if (index == -1) throw Exception("Product not found")

        // Truy cập document theo productId
        val productRef = cartRef.document(userId).collection("products").document(productId)
        println("Quantity: ${quantity}")
        if (quantity <= 0) {
            // Xóa document nếu quantity <= 0
            productRef.delete().await()
        } else {
            // Cập nhật quantity nếu quantity > 0
            productRef.update("quantity", quantity).await()
        }

        true to index // Trả về kết quả
    }
}
