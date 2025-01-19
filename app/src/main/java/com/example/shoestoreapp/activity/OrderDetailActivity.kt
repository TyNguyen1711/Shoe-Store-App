package com.example.shoestoreapp.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ConfirmingAdapter
import com.example.shoestoreapp.adapter.ItemAdapter
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.OrderMain
import com.example.shoestoreapp.data.model.OrderMainMain
import com.example.shoestoreapp.data.repository.OrderRepository
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class OrderDetailActivity : AppCompatActivity() {
    private var idOrder: String = ""
    private lateinit var orderAdapter: ItemAdapter
    private lateinit var rvOrders: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_detail)

        val estimatedDate: TextView = findViewById(R.id.estimatedTV)
        val shipInfo: TextView = findViewById(R.id.shipInfoTV)
        val shipTime: TextView = findViewById(R.id.shipTimeTV)
        val addressInfo: TextView = findViewById(R.id.addressInfoTV)
        val addressMainInfo: TextView = findViewById(R.id.addressMainInfoTV)
        val allProduct: TextView = findViewById(R.id.allProductTV)
        val coupon: TextView = findViewById(R.id.couponTV)
        val shipCost: TextView = findViewById(R.id.shipTV)
        val total: TextView = findViewById(R.id.totalTV)
        val orderID: TextView = findViewById(R.id.orderId)
        val paymentMethod: TextView = findViewById(R.id.paymentMethodTV)
        val orderedTime: TextView = findViewById(R.id.orderedTimeTV)
        val currentDateTime = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val dateTimeFormatterExcept = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val updatedDateTime = currentDateTime.plusDays(2)
        val plusupdateDateTime = currentDateTime.plusDays(4)
        val formattedDateTime = currentDateTime.format(dateTimeFormatter)
        val plusformattedDateTime = updatedDateTime.format(dateTimeFormatter)
        val plusplusformattedDateTime = plusupdateDateTime.format(dateTimeFormatter)
        val plusformattedDateTimeExcept = updatedDateTime.format(dateTimeFormatterExcept)
        val plusplusformattedDateTimeExcept = plusupdateDateTime.format(dateTimeFormatterExcept)
        val message:TextView = findViewById(R.id.messageTV)
        val receivedTime: TextView = findViewById(R.id.receivedTimeTV)
        val recTimeText: TextView = findViewById(R.id.textView8)

        rvOrders = findViewById<RecyclerView>(R.id.rvItems)
        rvOrders.layoutManager = LinearLayoutManager(this)

        idOrder = intent.getStringExtra("idOrder").toString()
        val type = intent.getStringExtra("type").toString()
        Log.d("type", type.toString())

        lifecycleScope.launch {
            val orders = getFirstOrder()
            val product = orders?.products
            if (product != null) {
                val productList = product.map {
                    OrderMainMain(
                    it.productId,
                    it.quantity,
                    it.size
                )
                }
                orderAdapter = ItemAdapter(productList)
                rvOrders.adapter = orderAdapter
            }
            estimatedDate.text = "Estimated Date Received: " + plusformattedDateTimeExcept.toString() + " - " + plusplusformattedDateTimeExcept.toString()
            shipTime.text = formattedDateTime.toString()
            addressInfo.text = orders?.recipientName + orders?.recipientPhone
            addressMainInfo.text = orders?.recipientAddress
            if (orders?.totalPayment != null) {
                allProduct.text = (orders.totalPayment + orders.voucher).toString() + "đ"
                coupon.text =   "  -"+ orders.voucher.toString() + "đ"
                shipCost.text = "0đ"
                total.text = "Total: " + orders.totalPayment.toString() + "đ"
            }
            orderID.text = "ID: " + orders?.id
            paymentMethod.text = orders?.paymentMethod
            orderedTime.text = orders?.orderTime
            message.text = orders?.message
            receivedTime.text = formattedDateTime.toString()
        }
        if (type == "confirming" || type == "delivering") {
            receivedTime.visibility = GONE
            recTimeText.visibility = GONE
            if (type == "confirming") {
                shipInfo.text = "Order is pending confirmation!"
            }
        }
        if (type == "received") {
            estimatedDate.visibility = GONE
            shipInfo.text = "Your order has been delivered to you!"
        }


        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun getFirstOrder(): Order? {
        val repository = OrderRepository()
        val allOrders = repository.getOrders()
        return allOrders.firstOrNull { it.id == idOrder }
    }
}


