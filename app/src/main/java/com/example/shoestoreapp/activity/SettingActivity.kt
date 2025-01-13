
package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoestoreapp.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ordersSection = findViewById<LinearLayout>(R.id.option_orders)
        val myDetailsSection = findViewById<LinearLayout>(R.id.option_myDetail)
        val deliveryAddressSection = findViewById<LinearLayout>(R.id.option_delivery)
        val paymentMethodsSection = findViewById<LinearLayout>(R.id.option_payment)
        val couponSection = findViewById<LinearLayout>(R.id.option_coupon)
        val notificationsSection = findViewById<LinearLayout>(R.id.option_notification)
        val helpSection = findViewById<LinearLayout>(R.id.option_help)
        val aboutSection = findViewById<LinearLayout>(R.id.option_about)
        val logoutButton = findViewById<Button>(R.id.my_button)

        ordersSection.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

//        myDetailsSection.setOnClickListener {
//            startActivity(Intent(this, MyDetailsActivity::class.java))
//        }
//
//        deliveryAddressSection.setOnClickListener {
//            startActivity(Intent(this, DeliveryAddressActivity::class.java))
//        }

        paymentMethodsSection.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        couponSection.setOnClickListener {
            startActivity(Intent(this, CouponActivity::class.java))
        }

        notificationsSection.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        helpSection.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        aboutSection.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Xử lý sự kiện click cho nút Logout
//        logoutButton.setOnClickListener {
//            // Thêm logic logout tại đây, ví dụ như xóa session hoặc chuyển về màn hình đăng nhập
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()  // Kết thúc activity hiện tại
//        }
    }
}

