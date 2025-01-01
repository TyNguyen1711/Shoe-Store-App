package com.example.shoestoreapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CityAdapter

class SelectCityActivity : AppCompatActivity() {
    private lateinit var cityRecyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        // Danh sách các tỉnh thành
        val cities = listOf(
            "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
            "Huế", "Nha Trang", "Vũng Tàu", "Quảng Ninh", "Bình Dương"
        )

        // Cấu hình RecyclerView
        cityRecyclerView = findViewById(R.id.cityRecyclerView)
        cityAdapter = CityAdapter(cities) { selectedCity ->
            Toast.makeText(this, "Chọn tỉnh: $selectedCity", Toast.LENGTH_SHORT).show()
        }
        cityRecyclerView.layoutManager = LinearLayoutManager(this)
        cityRecyclerView.adapter = cityAdapter
    }
}