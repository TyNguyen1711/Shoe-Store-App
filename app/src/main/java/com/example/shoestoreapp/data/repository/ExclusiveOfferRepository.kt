package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class ExclusiveOfferRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val exclusiveOfferCollection: CollectionReference = firestore.collection("exclusive offer")
    private val productCollection: CollectionReference = firestore.collection("products")

    suspend fun createExclusiveOfferCollection(): Result<Unit> = runCatching {
        val snapshot = productCollection
            .orderBy("salePercentage", Query.Direction.DESCENDING)
            .limit(8)
            .get()
            .await()

        for (product in snapshot.documents) {
            val data = product.data // Trích xuất dữ liệu thực tế
            if (data != null) {
                exclusiveOfferCollection.document(product.id).set(data).await()
            }
        }
    }

    suspend fun getProduct(id: String): Result<Product> = runCatching {
        val document = exclusiveOfferCollection.document(id).get().await()
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

    suspend fun updateProductAverageRating(productId: String, averageRating: Double): Result<Unit> = runCatching {
        val documentRef = exclusiveOfferCollection.document(productId)
        val snapshot = documentRef.get().await()

        if (snapshot.exists()) {
            documentRef.update("averageRating", averageRating).await()
        } else {
            throw Exception("Document with productId $productId does not exist.")
        }
    }


    suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        val snapshot = exclusiveOfferCollection.get().await()

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
            Log.d("Exclusive", "Product: ${documentData["averageRating"]}")
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
        exclusiveOfferCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(id: String): Result<Unit> = runCatching {
        exclusiveOfferCollection.document(id).delete().await()
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = runCatching {
        val snapshot = exclusiveOfferCollection
            .whereEqualTo("category_id", categoryId)
            .get()
            .await()
        snapshot.toObjects(Product::class.java)
    }

    suspend fun getProductsByBrand(brandId: String): Result<List<Product>> = runCatching {
        val snapshot = exclusiveOfferCollection
            .whereEqualTo("brand_id", brandId)
            .get()
            .await()
        // Ánh xạ tài liệu thành danh sách Product và xử lý variants riêng
        snapshot.documents.map { document ->
            val documentData = document.data ?: throw Exception("Product not found")

            // Lấy danh sách variants và ánh xạ từng phần tử
            val variants =
                (documentData["variants"] as? List<Map<String, Any>>)?.map { variantMap ->
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

    suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        val snapshot = exclusiveOfferCollection
            .orderBy("name")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .get()
            .await()
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
}