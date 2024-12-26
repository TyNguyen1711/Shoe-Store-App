package com.example.shoestoreapp.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CouponAdapter
import com.example.shoestoreapp.data.model.Coupon

class CouponActivity : AppCompatActivity() {
    private lateinit var adapter: CouponAdapter
    private lateinit var btnClearSelection: Button
    private lateinit var btnFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coupon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_coupons)
        btnClearSelection = findViewById(R.id.btn_clear_selection)
        btnFinish = findViewById(R.id.btn_finish)

        val coupons = listOf(
            Coupon("1","WELCOME200", "Add items worth $2 more to unlock", "Get 50% OFF", 5),
            Coupon("2","SUMMER50", "Minimum purchase $10", "Get 50% OFF on summer collection", 8),
            Coupon("3", "FIRST10", "First time users", "Get $10 OFF on first purchase", 5),
            Coupon("4","HOLIDAY25", "Holiday special", "25% OFF on all items", 6)
        )

        adapter = CouponAdapter(
            coupons = coupons,
            onCouponSelected = { coupon ->
                btnClearSelection.visibility = View.VISIBLE
                btnFinish.visibility = View.VISIBLE
            },
            onCouponDeselected = {
                btnClearSelection.visibility = View.GONE
                btnFinish.visibility = View.GONE
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

        btnClearSelection.setOnClickListener {
            adapter.clearSelection()
            btnClearSelection.visibility = View.GONE
            btnFinish.visibility = View.GONE
        }


        btnFinish.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}