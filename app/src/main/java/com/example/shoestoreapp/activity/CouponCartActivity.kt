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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CouponAdapter
import com.example.shoestoreapp.adapter.CouponCartAdapter
import com.example.shoestoreapp.data.model.Coupon
import com.example.shoestoreapp.fragment.MyCartFragment

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

        val coupons = listOf(
            Coupon("1","WELCOME200", "Enable to use", 3, 5),
            Coupon("2","SUMMER50", "Enable to use", 2, 8),
            Coupon("3", "FIRST10", "Enable to use", 2, 5),
            Coupon("4","HOLIDAY25", "Enable to use", 2, 6)
        )

        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: arrayListOf()
        println("Checked Product IDs: $selectedProductIds")

        adapter = CouponCartAdapter(
            coupons = coupons,
            onCouponSelected = { coupon ->
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("selectedCoupon", coupon.code)  // Truyền mã coupon
                intent.putExtra("discountCoupon", coupon.discount.toString()) // Truyền giảm giá
                intent.putStringArrayListExtra("selectedProductIds", ArrayList(selectedProductIds))
                Log.d("Info Coupon: ", coupon.toString())
                startActivity(intent)
            }
        )


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

    }

    // Hàm chuyển đổi fragment
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}