package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference

class CategoryRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val categoriesCollection: CollectionReference = firestore.collection("categories")

    suspend fun createCategory(category: Category): Result<Category> = runCatching {
        val documentReference = categoriesCollection.add(category).await()
        category.copy(id = documentReference.id)
    }

    suspend fun getCategory(id: String): Result<Category> = runCatching {
        val document = categoriesCollection.document(id).get().await()
        document.toObject(Category::class.java) ?: throw Exception("Category not found")
    }

    suspend fun getAllCategories(): Result<List<Category>> = runCatching {
        val snapshot = categoriesCollection.get().await()
        snapshot.toObjects(Category::class.java)
    }

    suspend fun updateCategory(category: Category): Result<Unit> = runCatching {
        categoriesCollection.document(category.id).set(category).await()
    }

    suspend fun deleteCategory(id: String): Result<Unit> = runCatching {
        categoriesCollection.document(id).delete().await()
    }
}