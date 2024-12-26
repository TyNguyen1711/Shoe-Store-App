package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val productsCollection: CollectionReference = firestore.collection("products")
    private val categoriesCollection: CollectionReference = firestore.collection("categories")
    private val brandsCollection: CollectionReference = firestore.collection("brands")

    suspend fun createProductWithNames(
        product: Product,
        categoryName: String,
    ): Result<Product> = runCatching {
        val categorySnapshot = categoriesCollection
            .whereEqualTo("name", categoryName)
            .get()
            .await()
        val categoryId = categorySnapshot.documents.firstOrNull()?.id
            ?: throw Exception("Category not found: $categoryName")


        val productToCreate = product.copy(
            categoryId = categoryId,
        )

        val documentReference = productsCollection.add(productToCreate).await()
        productToCreate.copy(id = documentReference.id)
    }

    suspend fun getProduct(id: String): Result<Product> = runCatching {
        val document = productsCollection.document(id).get().await()
        document.toObject(Product::class.java) ?: throw Exception("Product not found")
    }

    suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        val snapshot = productsCollection.get().await()
        snapshot.toObjects(Product::class.java)
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
        snapshot.toObjects(Product::class.java)
    }

    suspend fun getProductsByBrand(brandId: String): Result<List<Product>> = runCatching {
        val snapshot = productsCollection
            .whereEqualTo("brand_id", brandId)
            .get()
            .await()
        snapshot.toObjects(Product::class.java)
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        val snapshot = productsCollection
            .orderBy("name")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .get()
            .await()
        snapshot.toObjects(Product::class.java)
    }


}