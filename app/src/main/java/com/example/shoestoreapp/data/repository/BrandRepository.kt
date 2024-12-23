package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Brand
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference

class BrandRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val brandsCollection: CollectionReference = firestore.collection("brands")

    suspend fun createBrand(brand: Brand): Result<Brand> = runCatching {
        val documentReference = brandsCollection.add(brand).await()
        brand.copy(id = documentReference.id)
    }

    suspend fun getBrand(id: String): Result<Brand> = runCatching {
        val document = brandsCollection.document(id).get().await()
        document.toObject(Brand::class.java) ?: throw Exception("Brand not found")
    }

    suspend fun getAllBrands(): Result<List<Brand>> = runCatching {
        val snapshot = brandsCollection.get().await()
        snapshot.toObjects(Brand::class.java)
    }

    suspend fun updateBrand(brand: Brand): Result<Unit> = runCatching {
        brandsCollection.document(brand.id).set(brand).await()
    }

    suspend fun deleteBrand(id: String): Result<Unit> = runCatching {
        brandsCollection.document(id).delete().await()
    }
}