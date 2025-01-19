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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CouponCartActivity : AppCompatActivity() {
    private lateinit var adapter: CouponCartAdapter
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

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
        var totalPrice: String = "0"

        lifecycleScope.launch {
            val coupons = getCouponsList()
            totalPrice = intent.getStringExtra("totalPrice").toString()
            Log.d("totalPrice", totalPrice)
            adapter = CouponCartAdapter(
                coupons = coupons,
                totalPrice = totalPrice,
                onCouponSelected = { coupon ->
                    val pay = intent?.getStringExtra("pay").toString()
                    val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: arrayListOf()
                    var intent = Intent(this@CouponCartActivity, HomeActivity::class.java)
                    if (pay == "pay") {
                        intent = Intent(this@CouponCartActivity, PayActivity::class.java)
                        Log.d("Pay", "Pay type")
                    }
                    intent.putExtra("code", coupon.code)  // Truyền mã coupon
                    intent.putExtra("discount", coupon.discount.toString()) // Truyền giảm giá
                    intent.putStringArrayListExtra("selectedProductIds", ArrayList(selectedProductIds))
                    Log.d("List", ArrayList(selectedProductIds).toString())
                    intent.putExtra("userId", userId)
                    Log.d("Info Coupon: ", coupon.toString())
                    startActivity(intent)
                }
            )
            recyclerView.adapter = adapter
        }

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

    }

    private suspend fun getCouponsList(): List<Coupon> {
        val repository = CouponRepository()
        return repository.getAllCoupons()
    }

    // Hàm chuyển đổi fragment
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}