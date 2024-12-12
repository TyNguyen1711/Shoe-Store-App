package com.example.shoestoreapp.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.Notification
import com.example.shoestoreapp.classes.NotificationGroup
import com.example.shoestoreapp.classes.NotificationGroupAdapter

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mainRecyclerView: RecyclerView = findViewById(R.id.mainRecyclerView)
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        // Sample notification data
        val notificationsToday = listOf(
            Notification("Order Shipped", "Your order has been shipped.", "1h"),
            Notification("Flash Sale Alert", "Don't miss our flash sale!", "1h"),
            Notification("Product Review Request", "Please review your recent purchase.", "1h")
        )

        val notificationsYesterday = listOf(
            Notification("Order Shipped", "Your order has been shipped.", "1d"),
            Notification("New Paypal Added", "Your Paypal account has been added.", "1d")
        )

        val notificationGroups = listOf(
            NotificationGroup("Today", notificationsToday),
            NotificationGroup("Yesterday", notificationsYesterday)
        )

        val adapter = NotificationGroupAdapter(notificationGroups)
        mainRecyclerView.adapter = adapter


        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }
    }
}