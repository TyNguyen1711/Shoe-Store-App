package com.example.shoestoreapp.data.repository

import com.example.shoestoreapp.data.model.Coupon
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class CouponRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val couponCollection = firestore.collection("voucher")

    suspend fun getAllCoupons(): Result<MutableList<Coupon>> {
        return try {
            val snapshot = couponCollection.get().await()
            val coupons = snapshot.documents.mapNotNull { it.toObject<Coupon>() }.toMutableList()
            Result.success(coupons)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun addCoupon(coupon: Coupon): Result<Unit> {
        return try {
            couponCollection.add(coupon).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateCoupon(coupon: Coupon): Result<Unit> {
        return try {
            couponCollection.document(coupon.id).set(coupon).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCoupon(couponId: String): Result<Unit> {
        return try {
            couponCollection.document(couponId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}