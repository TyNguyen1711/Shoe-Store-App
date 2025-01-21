package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class BestSellingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val bestSellingCollection: CollectionReference = firestore.collection("best selling")
    private val productCollection: CollectionReference = firestore.collection("products")

    suspend fun createBestSellingCollection(): Result<Unit> = runCatching {
        val snapshot = productCollection
            .orderBy("soldCount", Query.Direction.DESCENDING)
            .limit(8)
            .get()
            .await()

        for (product in snapshot.documents) {
            val data = product.data // Trích xuất dữ liệu thực tế

            if (data != null) {
                // Chỉ set dữ liệu thực tế mà không sao chép toàn bộ DocumentSnapshot
                bestSellingCollection.document(product.id).set(data).await()
            }
        }
    }

    suspend fun getProduct(id: String): Result<Product> = runCatching {
        val document = bestSellingCollection.document(id).get().await()
        val documentData = document.data ?: throw Exception("Product not found")

        // Lấy danh sách variants và ánh xạ từng phần tử
        val variants = (documentData["variants"] as? List<Map<String, Any>>)?.map {
            ProductVariant(
                id = it["id"] as? String ?: "",
                size = it["size"] as? String ?: "",
                stock = (it["stock"] as? Number)?.toInt() ?: 0
            )
        } ?: emptyList()

        // Trả về Product với danh sách variants đã được xử lý
        Product(
            id = document.id,
            averageRating = (document["averageRating"] as? Number)?.toDouble() ?: 0.0,
            name = documentData["name"] as? String ?: "",
            description = documentData["description"] as? String ?: "",
            price = (documentData["price"] as? Number)?.toDouble() ?: 0.0,
            categoryId = documentData["categoryId"] as? String ?: "",
            brand = documentData["brand"] as? String ?: "",
            variants = variants,
            thumbnail =  documentData["thumbnail"] as? String?: "",
            images = documentData["images"] as? List<String> ?: emptyList(),
            soldCount = (documentData["soldCount"] as? Number)?.toInt() ?: 0,
            salePercentage = (document["salePercentage"] as? Number)?.toInt() ?: 0
        )
    }

    suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        val snapshot = bestSellingCollection.get().await()

        // Ánh xạ tài liệu thành danh sách Product và xử lý variants riêng
        snapshot.documents.map { document ->
            val documentData = document.data ?: throw Exception("Product not found")

            // Lấy danh sách variants và ánh xạ từng phần tử
            val variants = (documentData["variants"] as? List<Map<String, Any>>)?.map { variantMap ->
                ProductVariant(
                    id = variantMap["id"] as? String ?: "",
                    size = variantMap["size"] as? String ?: "",
                    stock = (variantMap["stock"] as? Number)?.toInt() ?: 0
                )
            } ?: emptyList()

            // Trả về Product với danh sách variants đã được xử lý
            Product(
                id = document.id,
                averageRating = (document["averageRating"] as? Number)?.toDouble() ?: 0.0,
                name = documentData["name"] as? String ?: "",
                description = documentData["description"] as? String ?: "",
                price = (documentData["price"] as? Number)?.toDouble() ?: 0.0,
                categoryId = documentData["categoryId"] as? String ?: "",
                brand = documentData["brand"] as? String ?: "",
                variants = variants,
                thumbnail =  documentData["thumbnail"] as? String?: "",
                images = documentData["images"] as? List<String> ?: emptyList(),
                soldCount = (documentData["soldCount"] as? Number)?.toInt() ?: 0,
                salePercentage = (document["salePercentage"] as? Number)?.toInt() ?: 0
            )
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        bestSellingCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(id: String): Result<Unit> = runCatching {
        bestSellingCollection.document(id).delete().await()
    }
}