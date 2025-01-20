package com.example.shoestoreapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.DeliveringAdapter
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.OrderMain
import com.example.shoestoreapp.data.repository.OrderRepository
import kotlinx.coroutines.launch

class DeliveringFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var orderAdapter: DeliveringAdapter
    private lateinit var orderRepository: OrderRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.delivering, container, false)

        rvOrders = view.findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val orders = getListOrders()
            val orderToShow = orders.map {
                OrderMain(
                it.id,
                it.totalPayment,
                it.orderTime,
                it.products
            )
            }
            Log.d("get", orders.toString())
            orderAdapter = DeliveringAdapter(orderToShow)
            rvOrders.adapter = orderAdapter
        }

        return view
    }

    private suspend fun getListOrders(): List<Order> {
        val repository = OrderRepository()
        val allOrders = repository.getOrders()
        allOrders.onSuccess { product ->
            return product.filter { it.status == "Shipping" }

        }.onFailure { error ->

        }
        return emptyList()
    }
}
