package com.example.shoestoreapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.OrderActivity
import com.example.shoestoreapp.activity.OrderDetailActivityMain
import com.example.shoestoreapp.data.model.OrderMain
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class DeliveringAdapter(private val orders: List<OrderMain>) : RecyclerView.Adapter<DeliveringAdapter.OrderViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        val rvItems: RecyclerView = view.findViewById(R.id.rvItems)
        val dateRec: TextView = view.findViewById(R.id.dateReceived)
        val receivedButton: Button = view.findViewById(R.id.receivedButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivering_item, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "ID: ${order.orderId}"
        val decimalFormat = DecimalFormat("#,###")
        val formattedAmount = decimalFormat.format(order.totalPrice)
        holder.totalPrice.text = "Total Price: ${formattedAmount}đ"
        holder.dateRec.text = "   Ordered date: " + order.dateRec

        // Set up inner RecyclerView
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = ItemAdapter(order.items)

        // Handle button click
        holder.receivedButton.setOnClickListener {
            // Cập nhật trạng thái đơn hàng thành "cancelled"
            val orderId = order.orderId
            val updatedOrderStatus = mapOf("status" to "Delivered")

            // Cập nhật trạng thái trong Firestore
            firestore.collection("orders").document(orderId)
                .update(updatedOrderStatus)
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Order ${order.orderId} cancelled", Toast.LENGTH_SHORT).show()
                    val intent = Intent(holder.itemView.context, OrderActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(holder.itemView.context, "Failed to cancel order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailActivityMain::class.java)
            intent.putExtra("type", "delivering")
            intent.putExtra("idOrder", order.orderId.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size
}

