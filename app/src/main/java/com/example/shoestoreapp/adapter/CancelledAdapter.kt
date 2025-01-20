package com.example.shoestoreapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.PayActivity
import com.example.shoestoreapp.data.model.OrderMain
import com.example.shoestoreapp.data.repository.ProductRepository
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CancelledAdapter(private val orders: List<OrderMain>) : RecyclerView.Adapter<CancelledAdapter.OrderViewHolder>() {

    val productRepository = ProductRepository()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        val rvItems: RecyclerView = view.findViewById(R.id.rvItems)
        val dateRec: TextView = view.findViewById(R.id.dateReceived)
        val receivedButton: Button = view.findViewById(R.id.receivedButton)
        val dateCancelled: TextView = view.findViewById(R.id.dateCancelled)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cancelled_item, parent, false)
        return OrderViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "ID: ${order.orderId}"
        val decimalFormat = DecimalFormat("#,###")
        val formattedAmount = decimalFormat.format(order.totalPrice)
        holder.totalPrice.text = "Total Price: ${formattedAmount}đ"
        holder.dateRec.text = "   Ordered date: " + order.dateRec

        val currentDateTime = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formattedDateTime = currentDateTime.format(dateTimeFormatter)
        holder.dateCancelled.text = "   Cancelled date: " + formattedDateTime.toString()

        // Set up inner RecyclerView
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = ItemAdapter(order.items)

        // Handle button click
        holder.receivedButton.setOnClickListener {
            // Chuyển sang PayActivity khi nhấn vào "Mua lại"
            val context = holder.itemView.context
            val intent = Intent(context, PayActivity::class.java)

            // Truyền các thông tin cần thiết (ví dụ: danh sách sản phẩm và tổng giá trị)
            intent.putStringArrayListExtra("selectedProductIds", ArrayList(order.items.map { it.productId }))
//            Log.d("id", ArrayList(order.items.map { it.productId }).toString())
            intent.putExtra("quantities", ArrayList(order.items.map { it.quantity }))
//            Log.d("quan", ArrayList(order.items.map { it.quantity }).toString())
            intent.putStringArrayListExtra("sizes", ArrayList(order.items.map { it.size }))
//            Log.d("size", ArrayList(order.items.map { it.size }).toString())
            intent.putExtra("source", "buy again")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size
}

