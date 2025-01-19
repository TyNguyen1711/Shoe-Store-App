package com.example.shoestoreapp.data.model

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R

data class OrderMain(
    val orderId: String = "",           // Mã đơn hàng
    val totalPrice: Int = 0,            // Tổng giá trị đơn hàng
    val dateRec: String = "",           // Ngày dự kiến nhận hàng
    val items: List<OrderMainMain> = listOf() // Danh sách mặt hàng
)