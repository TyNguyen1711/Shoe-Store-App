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
        val document = wishlistsCollection.document(userId).get().await()
        document.toObject(Wishlist::class.java)?: throw Exception("Product not found")
    }

    suspend fun updateUserWishlist(wishlist: Wishlist): Result<Unit> = runCatching {
        wishlistsCollection.document(wishlist.userId).set(wishlist).await()
    }

    suspend fun deleteUserWishlist(userId: String): Result<Unit> = runCatching {
        wishlistsCollection.document(userId).delete().await()
    }

    suspend fun addUserWishlist(wishlist: Wishlist): Result<Unit> = runCatching {
        wishlistsCollection.add(wishlist).await()
    }
}