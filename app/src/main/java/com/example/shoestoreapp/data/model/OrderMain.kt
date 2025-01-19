package com.example.shoestoreapp.data.model

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R

data class OrderMain(
    val orderId: String = "",           // Mã đơn hàng
    val totalPrice: Double = 0.0,            // Tổng giá trị đơn hàng
    val dateRec: String = "",           // Ngày dự kiến nhận hàng
    val items: List<ProductItem> = listOf() // Danh sách mặt hàng
)