package com.example.shoestoreapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.OrderDetailActivityMain
import com.example.shoestoreapp.data.model.OrderMain
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceivedAdapter(private val orders: List<OrderMain>) : RecyclerView.Adapter<ReceivedAdapter.OrderViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        val rvItems: RecyclerView = view.findViewById(R.id.rvItems)
        val dateRec: TextView = view.findViewById(R.id.dateReceived)
        val reportButton: Button = view.findViewById(R.id.receivedButton)
        val dateRecReal: TextView = view.findViewById(R.id.dateReceivedReal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.received_item, parent, false)
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
        holder.dateRecReal.text = "   Received date: " + formattedDateTime.toString()


        // Set up inner RecyclerView
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = ItemAdapter(order.items)

        // Handle button click
        holder.reportButton.setOnClickListener {

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailActivityMain::class.java)
            intent.putExtra("type", "received")
            intent.putExtra("idOrder", order.orderId.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size
}

