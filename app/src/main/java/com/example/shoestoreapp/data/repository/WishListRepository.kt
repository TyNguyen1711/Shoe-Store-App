package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.example.shoestoreapp.data.model.Wishlist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class WishListRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
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
}