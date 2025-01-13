package com.example.shoestoreapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R


class PaymentActivity : AppCompatActivity()  {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        var hideStatus: Boolean = false
        val wallet = findViewById<TextView>(R.id.balanceWallet)
        val hideEye = findViewById<ImageView>(R.id.hideEyeImg)
        val money: String = "300.000"

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }

        hideEye.setOnClickListener{
            hideStatus = !hideStatus
            if (hideStatus) {
                wallet.text = "₫ $money"
            }
            else {
                wallet.text = "₫ ***"
            }
        }

    }
}

