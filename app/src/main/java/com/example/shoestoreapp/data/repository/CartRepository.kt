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
            // Truy vấn các document có cùng productId và size
            val querySnapshot = cartRef.document(userId)
                .collection("products")
                .whereEqualTo("productId", product.productId)
                .whereEqualTo("size", product.size)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                // Nếu đã tồn tại document với productId và size, cập nhật quantity
                val existingDoc = querySnapshot.documents.first()
                val updatedQuantity = (existingDoc.getLong("quantity") ?: 0L) + product.quantity
                cartRef.document(userId)
                    .collection("products")
                    .document(existingDoc.id)
                    .update("quantity", updatedQuantity)
                    .await()
            } else {
                // Nếu không có document nào khớp, tạo document mới
                cartRef.document(userId)
                    .collection("products")
                    .add(product.toMap())
                    .await()
            }
        } catch (e: Exception) {
            throw e // Ném lỗi ra để Result ghi nhận
        }
        true
    }

    // Cập nhật số lượng sản phẩm
    suspend fun updateProductQuantity(
        userId: String,
        productId: String,
        productSize: String,
        quantity: Int
    ): Result<Pair<Boolean, Int>> = runCatching {
        // Lấy tất cả tài liệu trong collection "products" thuộc userId
        val productsSnapshot = cartRef.document(userId).collection("products").get().await()

        // Lấy danh sách tất cả các document
        val productList = productsSnapshot.documents

        // Tìm index của document dựa trên productId và size
        val index = productList.indexOfFirst { document ->
            document.getString("productId") == productId && document.getString("size") == productSize
        }

        if (index == -1) {
            throw Exception("Product not found with the specified size")
        }

        // Lấy reference tới document cần cập nhật
        val documentRef = productList[index].reference

        if (quantity <= 0) {
            // Xóa document nếu quantity <= 0
            documentRef.delete().await()
        } else {
            // Cập nhật quantity nếu quantity > 0
            documentRef.update("quantity", quantity).await()
        }

        // Trả về true và index của sản phẩm
        true to index
    }


    suspend fun removeProductFromCart(userId: String, idList: List<String>, sizeList: List<String>): Result<Pair<Boolean, List<Int>>> = runCatching {
        val productsSnapshot = cartRef.document(userId).collection("products").get().await()
        val productList = productsSnapshot.documents

        val idxList = mutableListOf<Int>()

        // Tạo Firestore batch
        val batch = cartRef.firestore.batch()

        // Duyệt qua từng productId trong idList
        for ((productId, size) in idList.zip(sizeList)) {
            // Duyệt tất cả các tài liệu trong productList và xóa những tài liệu có trường productId khớp
            val productDocsToDelete = productList.filterIndexed { _, document ->
                document.getString("productId") == productId && document.getString("size") == size
            }

            if (productDocsToDelete.isEmpty()) throw Exception("No product found with productId $productId")

            // Lặp qua các tài liệu cần xóa
            for (productDoc in productDocsToDelete) {
                // Lấy documentReference và thêm vào batch để xóa
                val productRef = cartRef.document(userId).collection("products").document(productDoc.id)
                batch.delete(productRef)

                // Lưu vị trí của sản phẩm đã xóa
                val originalIndex = productList.indexOf(productDoc)
                idxList.add(originalIndex)
            }
        }

        // Thực thi batch
        batch.commit().await()

        true to idxList // Trả về kết quả
    }

}
