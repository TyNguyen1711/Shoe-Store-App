package com.example.shoestoreapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CouponAdapter
import com.example.shoestoreapp.data.model.Coupon
import com.example.shoestoreapp.data.repository.CouponRepository
import kotlinx.coroutines.launch

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

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val coupons = getCouponsList()
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
            recyclerView.adapter = adapter
        }

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

    private suspend fun getCouponsList(): List<Coupon> {
        Log.e("Firestore", "Failed to fetch coupons!")
        val repository = CouponRepository()
        return repository.getAllCoupons()
    }
}