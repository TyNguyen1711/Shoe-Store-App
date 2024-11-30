package com.example.shoestoreapp.activity

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.CouponAdapter
import com.example.shoestoreapp.classes.CouponModel

class CouponActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coupon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val listView: ListView = findViewById(R.id.listview_coupons)

        // Sample coupon data
        val coupons = listOf(
            CouponModel("WELCOME200", "Add items worth $2 more to unlock", "Get 50% OFF"),
            CouponModel("SUMMER50", "Minimum purchase $10", "Get 50% OFF on summer collection"),
            CouponModel("FIRST10", "First time users", "Get $10 OFF on first purchase"),
            CouponModel("HOLIDAY25", "Holiday special", "25% OFF on all items"),
            CouponModel("WELCOME200", "Add items worth $2 more to unlock", "Get 50% OFF"),
            CouponModel("SUMMER50", "Minimum purchase $10", "Get 50% OFF on summer collection"),
            CouponModel("FIRST10", "First time users", "Get $10 OFF on first purchase"),
        )

        val adapter = CouponAdapter(this, coupons)
        listView.adapter = adapter
    }
}