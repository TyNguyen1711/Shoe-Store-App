package com.example.shoestoreapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.OrderProductsAdapter
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.ProductDetailItem
import com.example.shoestoreapp.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var tvOrderId: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvRecipientName: TextView
    private lateinit var tvRecipientPhone: TextView
    private lateinit var tvRecipientAddress: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvOrderTime: TextView
    private lateinit var tvTotalPayment: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvCurrentStatus: TextView
    private lateinit var spnNewStatus: Spinner
    private lateinit var btnUpdateStatus: Button
    private lateinit var rvOrderProducts: RecyclerView
    private lateinit var orderProductsAdapter: OrderProductsAdapter
    private lateinit var btnBack: Button
    private val repository = OrderRepository()
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết đơn hàng"

        orderId = intent.getStringExtra("ORDER_ID")
        initViews()
        setupRecyclerView()
        setupStatusSpinner()
        loadOrderDetails()
        setupUpdateButton()
        setupBackButton()
    }

    private fun initViews() {
        tvOrderId = findViewById(R.id.tvOrderId)
        tvUserId = findViewById(R.id.tvUserId)
        tvRecipientName = findViewById(R.id.tvRecipientName)
        tvRecipientPhone = findViewById(R.id.tvRecipientPhone)
        tvRecipientAddress = findViewById(R.id.tvRecipientAddress)
        tvMessage = findViewById(R.id.tvMessage)
        tvOrderTime = findViewById(R.id.tvOrderTime)
        tvTotalPayment = findViewById(R.id.tvTotalPayment)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus)
        spnNewStatus = findViewById(R.id.spnNewStatus)
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus)
        rvOrderProducts = findViewById(R.id.rvOrderProducts)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupRecyclerView() {
        orderProductsAdapter = OrderProductsAdapter()
        rvOrderProducts.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = orderProductsAdapter
        }
    }

    private fun setupStatusSpinner() {
        val statuses = arrayOf("Pending", "Confirmed", "Shipping", "Delivered", "Cancelled")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnNewStatus.adapter = adapter
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            val newStatus = spnNewStatus.selectedItem.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("NEW_STATUS", newStatus)
            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }
    }



    private fun loadOrderDetails() {
        orderId?.let { id ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = repository.getOrderDetailById(id)

                    withContext(Dispatchers.Main) {
                        when {
                            result.isSuccess -> {
                                val (order, products) = result.getOrNull()!!
                                displayOrderDetails(order, products)
                            }
                            result.isFailure -> {
                                Log.e("OrderDetail", "Failed to load order details", result.exceptionOrNull())
                                Toast.makeText(
                                    this@OrderDetailActivity,
                                    "Không tìm thấy đơn hàng",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("OrderDetail", "Error loading order details", e)
                        Toast.makeText(
                            this@OrderDetailActivity,
                            "Đã xảy ra lỗi khi tải thông tin đơn hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        } ?: run {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayOrderDetails(order: Order, products: List<ProductDetailItem>) {
        tvOrderId.text = "Mã đơn hàng: ${order.id}"
        tvUserId.text = "Mã khách hàng: ${order.userId}"
        tvRecipientName.text = "Người nhận: ${order.recipientName}"
        tvRecipientPhone.text = "Số điện thoại: ${order.recipientPhone}"
        tvRecipientAddress.text = "Địa chỉ: ${order.recipientAddress}"
        tvMessage.text = "Ghi chú: ${order.message}"
        tvOrderTime.text = "Thời gian đặt hàng: ${order.orderTime}"
        tvTotalPayment.text = "Tổng tiền: ${formatCurrency(order.totalPayment)}"
        tvPaymentMethod.text = "Phương thức thanh toán: ${order.paymentMethod}"
        tvCurrentStatus.text = "Trạng thái hiện tại: ${order.status}"

        // Đồng bộ Spinner với trạng thái hiện tại
        val statusArray = resources.getStringArray(R.array.order_statuses)
        val currentStatusPosition = statusArray.indexOf(order.status)
        if (currentStatusPosition != -1) {
            spnNewStatus.setSelection(currentStatusPosition)
        }

        orderProductsAdapter.submitList(products)
    }


    private fun setupUpdateButton() {
        btnUpdateStatus.setOnClickListener {
            val newStatus = spnNewStatus.selectedItem.toString()

            orderId?.let { id ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val result = repository.updateOrderStatus(id, newStatus)
                    withContext(Dispatchers.Main) {
                        if (result.isSuccess) {
                            tvCurrentStatus.text = "Trạng thái hiện tại: $newStatus"
                            spnNewStatus.setSelection(spnNewStatus.selectedItemPosition) // Đồng bộ Spinner
                            Toast.makeText(this@OrderDetailActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@OrderDetailActivity, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } ?: Toast.makeText(this, "Mã đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show()
        }

    }


    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}