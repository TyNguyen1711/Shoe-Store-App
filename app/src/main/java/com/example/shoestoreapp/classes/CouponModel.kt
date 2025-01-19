package com.example.shoestoreapp.classes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestoreapp.data.model.Coupon
import com.example.shoestoreapp.data.repository.CouponRepository
import kotlinx.coroutines.launch

data class CouponModel(
    val couponCode: String,
    val unlockMessage: String,
    val offer: String
)

class CouponViewModel(private val repository: CouponRepository) : ViewModel() {

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    fun fetchCouponsAsList() {
        viewModelScope.launch {
            try {
                val couponList = repository.getAllCoupons()
                _coupons.value = couponList
            } catch (e: Exception) {
                Log.e("CouponViewModel", "Error fetching coupons: ${e.message}")
            }
        }
    }
}

