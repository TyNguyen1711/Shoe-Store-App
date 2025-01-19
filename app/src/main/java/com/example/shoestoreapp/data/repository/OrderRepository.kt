package com.example.shoestoreapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductDetailItem
import com.example.shoestoreapp.data.model.ProductItem
import kotlinx.coroutines.tasks.await

class OrderRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val productsCollection = firestore.collection("products")
    suspend fun saveOrder(order: Order): Result<Boolean> {
        return try {
            ordersCollection.document(order.id).set(order).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy danh sách các đơn hàng từ Firestore
    suspend fun getOrders(): List<Order> {
        return try {
            val ordersRef = db.collection("orders") // Thay "orders" bằng tên collection trong Firestore của bạn
            val snapshot = ordersRef.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject<Order>()
            }
        } catch (e: Exception) {
            emptyList() // Trả về danh sách rỗng nếu có lỗi
        }
    }

    suspend fun getAllOrders(): Result<List<Pair<Order, List<ProductDetailItem>>>> = runCatching {
        val ordersSnapshot = ordersCollection.get().await()
        ordersSnapshot.documents.map { orderDocument ->
            val orderData = orderDocument.data ?: throw Exception("Order not found")

            val productItems = (orderData["products"] as? List<Map<String, Any>>)?.map { productMap ->
                ProductItem(
                    productId = productMap["productId"] as? String ?: "",
                    quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0,
                    size = productMap["size"] as? String ?: ""
                )
            } ?: emptyList()

            val productDetails = productItems.map { productItem ->
                val productSnapshot = productsCollection.document(productItem.productId).get().await()
                val productData = productSnapshot.data ?: throw Exception("Product not found: ${productItem.productId}")

                ProductDetailItem(
                    productId = productItem.productId,
                    quantity = productItem.quantity,
                    size = productItem.size,
                    name = productData["name"] as? String ?: "",
                    thumbnail = productData["thumbnail"] as? String ?: "",
                    price = (productData["price"] as? Number)?.toDouble() ?: 0.0
                )
            }

            Pair(
                Order(
                    id = orderDocument.id,
                    userId = orderData["userId"] as? String ?: "",
                    products = productItems,
                    totalPayment = (orderData["totalPayment"] as? Number)?.toDouble() ?: 0.0,
                    recipientName = orderData["recipientName"] as? String ?: "",
                    recipientPhone = orderData["recipientPhone"] as? String ?: "",
                    recipientAddress = orderData["recipientAddress"] as? String ?: "",
                    message = orderData["message"] as? String ?: "",
                    paymentMethod = orderData["paymentMethod"] as? String ?: "",
                    orderTime = orderData["orderTime"] as? String ?: "",
                    status = orderData["status"] as? String ?: "",
                    voucher = (orderData["voucher"] as? Number)?.toDouble() ?: 0.0
                ),
                productDetails
            )
        }
    }
    suspend fun getOrderDetailById(orderId: String): Result<Pair<Order, List<ProductDetailItem>>> = runCatching {
        val orderSnapshot = ordersCollection.document(orderId).get().await()
        val orderData = orderSnapshot.data ?: throw Exception("Order not found: $orderId")

        val productItems = (orderData["products"] as? List<Map<String, Any>>)?.map { productMap ->
            ProductItem(
                productId = productMap["productId"] as? String ?: "",
                quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0,
                size = productMap["size"] as? String ?: ""
            )
        } ?: emptyList()

        val productDetails = productItems.map { productItem ->
            val productSnapshot = productsCollection.document(productItem.productId).get().await()
            val productData = productSnapshot.data ?: throw Exception("Product not found: ${productItem.productId}")

            ProductDetailItem(
                productId = productItem.productId,
                quantity = productItem.quantity,
                size = productItem.size,
                name = productData["name"] as? String ?: "",
                thumbnail = productData["thumbnail"] as? String ?: "",
                price = (productData["price"] as? Number)?.toDouble() ?: 0.0
            )
        }

        Pair(
            Order(
                id = orderSnapshot.id,
                userId = orderData["userId"] as? String ?: "",
                products = productItems,
                totalPayment = (orderData["totalPayment"] as? Number)?.toDouble() ?: 0.0,
                recipientName = orderData["recipientName"] as? String ?: "",
                recipientPhone = orderData["recipientPhone"] as? String ?: "",
                recipientAddress = orderData["recipientAddress"] as? String ?: "",
                message = orderData["message"] as? String ?: "",
                paymentMethod = orderData["paymentMethod"] as? String ?: "",
                orderTime = orderData["orderTime"] as? String ?: "",
                status = orderData["status"] as? String ?: "",
                voucher = (orderData["voucher"] as? Number)?.toDouble() ?: 0.0
            ),
            productDetails
        )
    }
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Boolean> {
        return try {
            ordersCollection.document(orderId)
                .update("status", newStatus)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

