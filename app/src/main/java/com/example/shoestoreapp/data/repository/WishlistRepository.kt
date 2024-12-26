package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Brand
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.Wishlist
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WishlistRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val productsCollection: CollectionReference = firestore.collection("products")
    private val usersCollection: CollectionReference = firestore.collection("users")
    private val wishlistsCollection: CollectionReference = firestore.collection("wishlists")
    private val brandsCollection: CollectionReference = firestore.collection("brands")
    suspend fun addToWishlist(userId: String, productId: String): Result<Unit> {
        return try {
            val wishlistDocRef = wishlistsCollection.document(userId)
            val wishlistSnapshot = wishlistDocRef.get().await()

            if (wishlistSnapshot.exists()) {
                val currentProducts = wishlistSnapshot.get("products") as? List<String> ?: listOf()

                if (!currentProducts.contains(productId)) {
                    val updatedProducts = currentProducts.toMutableList()
                    updatedProducts.add(productId)
                    wishlistDocRef.update("products", updatedProducts).await()
                }
            } else {
                val newWishlist = Wishlist(userId = userId, products = listOf(productId))
                wishlistDocRef.set(newWishlist).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWishlistByUserId(userId: String): Result<List<Product>> {
        return try {
            val wishlistDocRef = wishlistsCollection.document(userId)
            val wishlistSnapshot = wishlistDocRef.get().await()

            if (!wishlistSnapshot.exists()) {
                return Result.success(emptyList())
            }

            val productIds = (wishlistSnapshot.get("products") as? List<String>) ?: emptyList()
            if (productIds.isEmpty()) {
                return Result.success(emptyList())
            }

            val products = mutableListOf<Product>()
            productIds.chunked(10).forEach { chunk ->
                val productSnapshots = productsCollection
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val fetchedProducts = productSnapshots.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }

                products.addAll(fetchedProducts)
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}