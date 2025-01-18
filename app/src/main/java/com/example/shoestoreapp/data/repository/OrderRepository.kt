package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository(private val firestore: FirebaseFirestore) {
    private val ordersCollection = firestore.collection("orders")

    suspend fun saveOrder(order: Order): Result<Boolean> {
        return try {
            ordersCollection.document(order.id).set(order).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrders(userId: String? = null): Result<List<Order>> {
        return try {
            val querySnapshot = if (userId != null) {
                // Nếu truyền userId, chỉ lấy danh sách order của user đó
                ordersCollection.whereEqualTo("userId", userId).get().await()
            } else {
                // Lấy tất cả orders
                ordersCollection.get().await()
            }

            val orders = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)?.apply {
                    id = document.id
                }
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}