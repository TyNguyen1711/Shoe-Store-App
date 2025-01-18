package com.example.shoestoreapp.data.repository


import android.util.Log
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.Wishlist
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class WishListRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val productsCollection: CollectionReference = firestore.collection("products")
    private val wishlistsCollection: CollectionReference = firestore.collection("wishlists")
    suspend fun getUserWishlist(userId: String): Result<Wishlist> = runCatching {
        val document = try {
            Log.d("WishlistRepo", "Fetching document for user: $userId")
            wishlistsCollection.document(userId).get().await()
        } catch (e: Exception) {
            Log.e("WishListRepository", "Error fetching document: ${e.message}")
            throw e
        }
        Log.d("WishListRepo2", "${document.data}")
        document.toObject(Wishlist::class.java)?: throw Exception("Product not found")
    }

    suspend fun updateUserWishlist(wishlist: Wishlist): Result<Unit> = runCatching {
        Log.d("WishlistRepo", "Updating document for user: ${wishlist}")
        wishlistsCollection.document(wishlist.userId).set(wishlist).await()
    }

    suspend fun deleteUserWishlist(userId: String): Result<Unit> = runCatching {
        wishlistsCollection.document(userId).delete().await()
    }

    suspend fun addUserWishlist(wishlist: Wishlist): Result<Unit> = runCatching {
        wishlistsCollection.document(wishlist.userId).set(wishlist).await()
    }

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
                val newWishlist = Wishlist(userId = userId, products = mutableListOf(productId))
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

    suspend fun removeFromWishlist(userId: String, productId: String): Result<Unit> {
        return try {
            val wishlistDocRef = wishlistsCollection.document(userId)
            val wishlistSnapshot = wishlistDocRef.get().await()

            if (!wishlistSnapshot.exists()) {
                return Result.failure(Exception("Wishlist not found"))
            }

            val currentProducts = wishlistSnapshot.get("products") as? List<String> ?: listOf()

            if (!currentProducts.contains(productId)) {
                return Result.failure(Exception("Product not found in wishlist"))
            }
            val updatedProducts = currentProducts.filter { it != productId }
            if (updatedProducts.isEmpty()) {
                wishlistDocRef.delete().await()
            } else {
                wishlistDocRef.update("products", updatedProducts).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}