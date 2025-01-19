package com.example.shoestoreapp.fragment
import OrdersAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.OrderDetailActivityMain
import com.example.shoestoreapp.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdminOrdersFragment : Fragment() {
    private lateinit var rvOrders: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private val repository = OrderRepository()
    private val REQUEST_CODE = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvOrders = view.findViewById(R.id.rvOrders)
        setupRecyclerView()
        loadOrders()
    }
    override fun onResume() {
        super.onResume()
        loadOrders()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter { order ->
            val intent = Intent(requireContext(), OrderDetailActivityMain::class.java)
            intent.putExtra("ORDER_ID", order.first.id)
            startActivityForResult(intent, REQUEST_CODE)
        }

        rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersAdapter
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getAllOrders()
                withContext(Dispatchers.Main) {
                    when {
                        result.isSuccess -> {
                            ordersAdapter.submitList(result.getOrNull() ?: emptyList())
                        }
                        result.isFailure -> {
                            Toast.makeText(
                                context,
                                "Lỗi khi tải đơn hàng",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Đã xảy ra lỗi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}