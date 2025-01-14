package com.example.shoestoreapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.Contact
import com.example.shoestoreapp.adapter.ContactAdapter
import java.util.*

class AddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_address)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val contactList = listOf(
            Contact("Trần Đức Minh  |  (+84) 123 456 789", "123 Đường ABC, Khu phố 4", "Phường A, Quận 1, TP. Hồ Chí Minh"),
            Contact("Phạm Nguyễn Thu Hương  |  (+84) 987 654 321", "456 Đường XYZ, Khu phố 11", "Phường B, Quận 3, TP. Hồ Chí Minh")
        )

        val adapter = ContactAdapter(contactList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        val addAddress = findViewById<ConstraintLayout>(R.id.block_4)
        addAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener{
            finish()
        }
    }


}
