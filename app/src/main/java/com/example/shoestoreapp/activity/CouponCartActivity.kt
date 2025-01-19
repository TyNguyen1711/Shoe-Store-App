package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CouponAdapter
import com.example.shoestoreapp.adapter.CouponCartAdapter
import com.example.shoestoreapp.data.model.Coupon
import com.example.shoestoreapp.data.repository.CouponRepository
import com.example.shoestoreapp.fragment.MyCartFragment
import kotlinx.coroutines.launch

class CouponCartActivity : AppCompatActivity() {
    private lateinit var adapter: CouponCartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coupon_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_coupons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: arrayListOf()
        val totalPrice = intent.getStringExtra("totalPrice")
        Log.d("totalPrice", totalPrice.toString())

        lifecycleScope.launch {
            val coupons = getCouponsList()
            if (coupons.isNotEmpty() && totalPrice != null) {
                adapter = CouponCartAdapter(
                    coupons = coupons,
                    totalPrice = totalPrice,
                    onCouponSelected = { coupon ->
                        val intent = Intent(this@CouponCartActivity, HomeActivity::class.java)
                        intent.putExtra("code", coupon.code) // Truyền mã coupon
                        intent.putExtra("discount", coupon.discount.toString()) // Truyền giảm giá
                        intent.putStringArrayListExtra("selectedProductIds", ArrayList(selectedProductIds))
                        Log.d("Info Coupon", coupon.toString())
                        startActivity(intent)
                    }
                )
                recyclerView.adapter = adapter
            } else {
                Log.e("CouponCartActivity", "No coupons available or total price is null")
            }
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private suspend fun getCouponsList(): List<Coupon> {
        val repository = CouponRepository()
        return repository.getAllCoupons()
    }
}
