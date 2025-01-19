package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val usersCollection: CollectionReference = firestore.collection("users")

    suspend fun createUser(user: User): Result<Unit> = runCatching {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun getUser(id: String): Result<User> = runCatching {
        val document = usersCollection.document(id).get().await()
        document.toObject(User::class.java) ?: throw Exception("User not found")
    }

    suspend fun getAllUsers(): Result<List<User>> = runCatching {
        val snapshot = usersCollection.get().await()
        snapshot.toObjects(User::class.java)
    }

    suspend fun updateUser(user: User): Result<Unit> = runCatching {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun deleteUser(id: String): Result<Unit> = runCatching {
        usersCollection.document(id).delete().await()
    }

    suspend fun updateSearchHistory(id: String, newHistory: List<String> = emptyList()): Result<Unit> = runCatching {
        val task = usersCollection.document(id).update("searchHistory", newHistory)
        task.await() // Chờ kết quả của Firestore task
        println("Search history updated successfully!")
    }

    // Hàm suspend để lấy từ khóa xuất hiện nhiều nhất
    suspend fun getMostFrequentSearchTerm(): String? {
        val searchHistoryCounts = mutableMapOf<String, Int>()

        try {
            // Truy xuất tất cả các document từ Firestore
            val result = usersCollection.get().await()

            for (document in result) {
                // Lấy danh sách searchHistory từ từng user
                val searchHistory = document.get("searchHistory") as? List<String> ?: continue
                println("Ming3993: $searchHistory")

                // Đếm tần suất xuất hiện của mỗi từ khóa
                for (term in searchHistory) {
                    val normalizedTerm = term.lowercase() // Chuyển chuỗi về chữ thường
                    searchHistoryCounts[normalizedTerm] = searchHistoryCounts.getOrDefault(normalizedTerm, 0) + 1
                }
            }

            // Tìm từ khóa xuất hiện nhiều nhất
            return searchHistoryCounts.maxByOrNull { it.value }?.key
        } catch (e: Exception) {
            println("Lỗi khi truy vấn Firestore: ${e.message}")
            return null
        }
    }

}