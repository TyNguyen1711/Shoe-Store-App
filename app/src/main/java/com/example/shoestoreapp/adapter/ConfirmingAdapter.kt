package com.example.shoestoreapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
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
import com.example.shoestoreapp.activity.OrderDetailActivity
import com.example.shoestoreapp.data.model.OrderMain
import com.google.firebase.firestore.FirebaseFirestore


class ConfirmingAdapter(private val orders: List<OrderMain>) : RecyclerView.Adapter<ConfirmingAdapter.OrderViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var fragmentManager: androidx.fragment.app.FragmentManager
    private lateinit var onOrderClick: (OrderMain) -> Unit

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        val rvItems: RecyclerView = view.findViewById(R.id.rvItems)
        val dateRec: TextView = view.findViewById(R.id.dateReceived)
        val receivedButton: Button = view.findViewById(R.id.cancelledButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.confirming_item, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DetachAndAttachSameFragment")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "ID: ${order.orderId}"
        holder.totalPrice.text = "Total Price: ${order.totalPrice}đ"
        holder.dateRec.text = "   Ordered date: " + order.dateRec

        // Set up inner RecyclerView
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = ItemAdapter(order.items)

        holder.receivedButton.setOnClickListener {
            // Cập nhật trạng thái đơn hàng thành "cancelled"
            val orderId = order.orderId
            val updatedOrderStatus = mapOf("status" to "Cancelled")

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
            val intent = Intent(holder.itemView.context, OrderDetailActivity::class.java)
            intent.putExtra("type", "confirming")
            intent.putExtra("idOrder", order.orderId.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size

}

