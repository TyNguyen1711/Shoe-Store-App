package com.example.shoestoreapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R


class OrderActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)

        // Tìm các ImageView
        val deliveringTextView = findViewById<TextView>(R.id.delivering)
        val receivedTextView = findViewById<TextView>(R.id.received)
        val cancelledTextView = findViewById<TextView>(R.id.cancelled)

        // Tìm các ImageView
        val deliveringImageView = findViewById<ImageView>(R.id.imageDelivering)
        val receivedImageView = findViewById<ImageView>(R.id.imageReceived)
        val cancelledImageView = findViewById<ImageView>(R.id.imageCancelled)

        // Tạo danh sách TextView và ImageView
        val textViews = listOf(deliveringTextView, receivedTextView, cancelledTextView)
        val imageViews = listOf(deliveringImageView, receivedImageView, cancelledImageView)

        // Khởi tạo StatusSwitcher với trạng thái mặc định là "delivering"
        StatusSwitcher(
            context = this,
            textViews = textViews,
            imageViews = imageViews,
            selectedBackground = R.drawable.corner_shape_chosen,
            defaultBackground = R.drawable.corner_shape_button,
            defaultSelected = deliveringTextView // Trạng thái ban đầu
        )

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }

    }
}

