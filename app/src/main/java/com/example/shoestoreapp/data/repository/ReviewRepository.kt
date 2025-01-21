package com.example.shoestoreapp.data.repository

import android.util.Log
import com.example.shoestoreapp.data.model.Comment
import com.example.shoestoreapp.data.model.Review
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class ReviewRepository (private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val reviewCollection: CollectionReference = firestore.collection("reviews")
    private val exclusiveOfferCollection: CollectionReference = firestore.collection("exclusive offer")
    private val bestSellingCollection: CollectionReference = firestore.collection("best selling")
    private val productsCollection: CollectionReference = firestore.collection("products")

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
        val document = reviewCollection.document(review.productId!!).get().await()
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
        var sum = 0.0
        for (comment in commentList) {
            sum += comment.rating
        }
        val avgRating = sum / commentList.size
        Log.d("Review product", "$avgRating")
        Log.d("Review product", "${review.productId}")

        // Cập nhật trong exclusiveOfferCollection nếu sản phẩm tồn tại
        try {
            val exclusiveRef = exclusiveOfferCollection.document(review.productId)
            val exclusiveSnapshot = exclusiveRef.get(Source.SERVER).await()
            if (exclusiveSnapshot.exists()) {
                Log.d("Review exclusive", "Updating exclusiveOffer for productId: ${review.productId}")
                exclusiveRef.update("averageRating", avgRating).await()
            } else {
                Log.w("Review exclusive", "Exclusive document not found for productId: ${review.productId}")
            }
        } catch (e: Exception) {
            Log.e("Review exclusive", "Error updating exclusiveOffer for productId: ${review.productId}", e)
        }

        // Cập nhật trong bestSellingCollection nếu sản phẩm tồn tại
        try {
            val bestSellingRef = bestSellingCollection.document(review.productId)
            val bestSellingSnapshot = bestSellingRef.get(Source.SERVER).await()
            if (bestSellingSnapshot.exists()) {
                Log.d("Review best selling", "Updating bestSelling for productId: ${review.productId}")
                bestSellingRef.update("averageRating", avgRating).await()
            } else {
                Log.w("Review best selling", "Best selling document not found for productId: ${review.productId}")
            }
        } catch (e: Exception) {
            Log.e("Review best selling", "Error updating bestSelling for productId: ${review.productId}", e)
        }

        // Cập nhật trong productCollection nếu sản phẩm tồn tại
        try {
            val productRef = productsCollection.document(review.productId)
            val productSnapshot = productRef.get(Source.SERVER).await()
            if (productSnapshot.exists()) {
                Log.d("Review product", "Updating productCollection for productId: ${review.productId}")
                productRef.update("averageRating", avgRating).await()
            } else {
                Log.w("Review product", "Product document not found for productId: ${review.productId}")
            }
        } catch (e: Exception) {
            Log.e("Review product", "Error updating productCollection for productId: ${review.productId}", e)
        }

        // Cập nhật review trong reviewCollection
        try {
            reviewCollection.document(review.productId).set(review).await()
        } catch (e: Exception) {
            Log.e("Review update", "Error updating reviewCollection for productId: ${review.productId}", e)
        }
    }

    suspend fun deleteReview(id: String): Result<Unit> = runCatching {
        reviewCollection.document(id).delete().await()
    }
}