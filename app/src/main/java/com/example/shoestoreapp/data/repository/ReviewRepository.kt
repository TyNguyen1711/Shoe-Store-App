package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Comment
import com.example.shoestoreapp.data.model.Review
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository (private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val reviewCollection: CollectionReference = firestore.collection("reviews")

    suspend fun createReview(review: Review): Result<Unit> = runCatching {
        reviewCollection.document(review.productId!!).set(review).await()

    }

    suspend fun getReview(id: String): Result<Review> = runCatching {
        val document = reviewCollection.document(id).get().await()
        val documentData = document.data ?: throw Exception("Review not found")
        // Lấy danh sách comment và ánh xạ từng phần tử
        val commentList = (documentData["commentList"] as? List<Map<String, Any>>)?.map { commentMap ->
            val rating = when (val ratingValue = commentMap["rating"]) {
                is Number -> ratingValue.toDouble()
                else -> 0.0
            }

            Comment(
                username = commentMap["username"] as? String ?: "",
                email = commentMap["email"] as? String ?: "",
                rating = rating,
                comment = commentMap["comment"] as? String ?: ""
            )
        } ?: emptyList()

        // Trả về Product với danh sách variants đã được xử lý
        Review(
            productId = document.id,
            commentList = commentList
        )
    }

    suspend fun updateReview(review: Review): Result<Unit> = runCatching {
        reviewCollection.document(review.productId!!).set(review).await()
    }

    suspend fun deleteReview(id: String): Result<Unit> = runCatching {
        reviewCollection.document(id).delete().await()
    }
}