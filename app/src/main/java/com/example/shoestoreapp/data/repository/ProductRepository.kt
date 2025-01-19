package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val productsCollection: CollectionReference = firestore.collection("products")
    private val categoriesCollection: CollectionReference = firestore.collection("categories")

    suspend fun createProductWithNames(
        product: Product,
        categoryName: String,
        brandName: String
    ): Result<Product> = runCatching {
        val categorySnapshot = categoriesCollection
            .whereEqualTo("name", categoryName)
            .get()
            .await()
        val categoryId = categorySnapshot.documents.firstOrNull()?.id
            ?: throw Exception("Category not found: $categoryName")

        val productToCreate = product.copy(
            categoryId = categoryId,
            brand = brandName
        )

        val documentReference = productsCollection.add(productToCreate).await()
        productToCreate.copy(id = documentReference.id)
    }

    suspend fun getProduct(id: String): Result<Product> = runCatching {
        val document = productsCollection.document(id).get().await()
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
            soldCount = documentData["soldCount"] as? Int ?: 0,
            salePercentage = document["salePercentage"] as? Int ?: 0
        )
    }

    suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        val snapshot = productsCollection
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

    suspend fun updateProductAverageRating(productId: String, averageRating: Double): Result<Unit> = runCatching {
        val documentRef = productsCollection.document(productId)
        val snapshot = documentRef.get().await()

        if (snapshot.exists()) {
            documentRef.update("averageRating", averageRating).await()
        } else {
            throw Exception("Document with productId $productId does not exist.")
        }
    }


    suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(id: String): Result<Unit> = runCatching {
        productsCollection.document(id).delete().await()
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = runCatching {
        val snapshot = productsCollection
            .whereEqualTo("category_id", categoryId)
            .get()
            .await()
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
                name = documentData["name"] as? String ?: "",
                description = documentData["description"] as? String ?: "",
                price = (documentData["price"] as? Number)?.toDouble() ?: 0.0,
                categoryId = documentData["categoryId"] as? String ?: "",
                brand = documentData["brand"] as? String ?: "",
                variants = variants,
                thumbnail =  documentData["thumbnail"] as? String?: "",
                images = documentData["images"] as? List<String> ?: emptyList()
            )
        }
    }
    suspend fun updateProductWithNames(
        product: Product,
        categoryName: String,
        brandName: String
    ): Result<Unit> = runCatching {
        val categorySnapshot = categoriesCollection
            .whereEqualTo("name", categoryName)
            .get()
            .await()
        val categoryId = categorySnapshot.documents.firstOrNull()?.id
            ?: throw Exception("Category not found: $categoryName")

        val productToUpdate = product.copy(
            categoryId = categoryId,
            brand = brandName
        )

        productsCollection.document(product.id).set(productToUpdate).await()
    }

    suspend fun getProductsByBrand(brand: String): Result<List<Product>> = runCatching {
        val snapshot = productsCollection
            .whereEqualTo("brand", brand) // Lọc sản phẩm có brand khớp với brandId
            .get()
            .await()
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



    suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        val snapshot = productsCollection.get().await()

        // Chia query thành danh sách từ khóa
        val keywords = query.split(" ").filter { it.isNotBlank() }

        // Lọc kết quả tại client dần dần theo từng từ khóa
        val filteredProducts = keywords.fold(snapshot.documents) { currentList, keyword ->
            currentList.filter { document ->
                val name = document.getString("name") ?: ""
                name.contains(keyword, ignoreCase = true) // Kiểm tra nếu tên chứa từ khóa
            }
        }

        // Ánh xạ kết quả vào danh sách sản phẩm
        filteredProducts.map { document ->
            val documentData = document.data ?: throw Exception("Product not found")
            Log.d("SNAPSHOT RESULT:", document.id)

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