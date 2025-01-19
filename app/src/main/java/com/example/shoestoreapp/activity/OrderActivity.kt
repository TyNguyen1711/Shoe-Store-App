package com.example.shoestoreapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CancelledAdapter
import com.example.shoestoreapp.fragment.ConfirmingFragment
import com.example.shoestoreapp.fragment.DeliveringFragment
import com.example.shoestoreapp.fragment.CancelledFragment
import com.example.shoestoreapp.fragment.ReceivedFragment


class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)

        // Tìm các ImageView
        val deliveringTextView = findViewById<TextView>(R.id.delivering)
        val receivedTextView = findViewById<TextView>(R.id.received)
        val cancelledTextView = findViewById<TextView>(R.id.cancelled)
        val confirmingTextView = findViewById<TextView>(R.id.confirming)

        // Tìm các ImageView
        val deliveringImageView = findViewById<ImageView>(R.id.imageDelivering)
        val receivedImageView = findViewById<ImageView>(R.id.imageReceived)
        val cancelledImageView = findViewById<ImageView>(R.id.imageCancelled)
        val confirmingImageView = findViewById<ImageView>(R.id.imageConfirming)

        // Tạo danh sách TextView và ImageView
        val textViews = listOf(confirmingTextView, deliveringTextView, receivedTextView, cancelledTextView)
        val imageViews = listOf(confirmingImageView, deliveringImageView, receivedImageView, cancelledImageView)

        // Tạo danh sách các Fragment
        val fragments = listOf(
            ConfirmingFragment(),
            DeliveringFragment(),
//            ReceivedFragment(),  // Bạn cần tạo fragment này nếu chưa có
//            CancelledFragment()   // Bạn cần tạo fragment này nếu chưa có
            ReceivedFragment(),
            CancelledFragment(),
        )

        // Khởi tạo StatusSwitcher với trạng thái mặc định là "confirming"
        StatusSwitcher(
            context = this,
            textViews = textViews,
            imageViews = imageViews,
            selectedBackground = R.drawable.corner_shape_chosen,
            defaultBackground = R.drawable.corner_shape_button,
            fragmentContainerId = R.id.fragmentContainer,
            fragmentManager = supportFragmentManager,
            fragments = fragments,
            defaultSelected = confirmingTextView // Trạng thái ban đầu
        )

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }
    }
}


