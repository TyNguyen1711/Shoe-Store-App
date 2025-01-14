package com.example.shoestoreapp.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R

class AddAddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_delivery_address)

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener{
            finish()
        }
    }


}
