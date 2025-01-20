package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Brand
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.model.Coupon
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class VoucherRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val couponsCollection: CollectionReference = firestore.collection("voucher")

    suspend fun createCouple(coupon: Coupon): Result<Coupon> = runCatching {
        val documentReference = couponsCollection.add(coupon).await()
        coupon.copy(id = documentReference.id)
    }

    suspend fun getCoupon(id: String): Result<Coupon> = runCatching {
        val document = couponsCollection.document(id).get().await()
        document.toObject(Coupon::class.java) ?: throw Exception("Coupon not found")
    }

    suspend fun getAllCoupons(): List<Coupon> {
        val snapshot = couponsCollection.get().await()
        return snapshot.toObjects(Coupon::class.java)
    }

    suspend fun updateCoupon(coupon: Coupon): Result<Unit> = runCatching {
        couponsCollection.document(coupon.id).set(coupon).await()
    }

    suspend fun deleteCoupon(id: String): Result<Unit> = runCatching {
        couponsCollection.document(id).delete().await()
    }

    suspend fun getCouponByCode(code: String): Result<Coupon> = runCatching {
        val querySnapshot = couponsCollection.whereEqualTo("code", code).get().await()
        val coupon = querySnapshot.documents.firstOrNull()?.toObject(Coupon::class.java)
            ?: throw Exception("Coupon not found")
        coupon
    }

    public final suspend fun decrementCouponQuantity(code: String): Result<Unit> = runCatching {
        // Tìm coupon dựa trên mã code
        val coupon = getCouponByCode(code).getOrNull() // Lấy coupon theo mã code

        if (coupon != null) {
            // Giảm quantity
            val updatedCoupon = coupon.copy(quantity = coupon.quantity - 1)

            // Nếu quantity = 0, xóa coupon khỏi Firestore
            if (updatedCoupon.quantity <= 0) {
                deleteCoupon(coupon.id) // Xóa coupon theo id
            } else {
                updateCoupon(updatedCoupon) // Cập nhật coupon với quantity mới
            }
        } else {
            throw Exception("Coupon not found")
        }
    }
}