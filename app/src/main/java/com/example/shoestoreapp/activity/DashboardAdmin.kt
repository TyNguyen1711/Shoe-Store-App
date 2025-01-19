package com.example.shoestoreapp.activity

import AdminUserFragment
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.shoestoreapp.R
import com.example.shoestoreapp.fragment.AdminOrdersFragment
import com.example.shoestoreapp.fragment.ProductManagementFragment
import com.example.shoestoreapp.fragment.ReportFragment
import com.example.shoestoreapp.fragment.VoucherManagementFragment

class DashboardAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ReportFragment())
            .commit()
        // Gán sự kiện cho các button
        findViewById<Button>(R.id.btnDashboard).setOnClickListener {
            switchFragment(ReportFragment())
        }
        // Gán sự kiện cho các button
        findViewById<Button>(R.id.btnProducts).setOnClickListener {
            switchFragment(ProductManagementFragment())
        }

        findViewById<Button>(R.id.btnVoucher).setOnClickListener {
            switchFragment(VoucherManagementFragment())
        }
        findViewById<Button>(R.id.btnUsers).setOnClickListener {
            switchFragment(AdminUserFragment())
        }
        findViewById<Button>(R.id.btnOrders).setOnClickListener {
            switchFragment(AdminOrdersFragment())
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}