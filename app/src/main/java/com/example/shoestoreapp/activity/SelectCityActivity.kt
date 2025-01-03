package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CityAdapter

class SelectCityActivity : AppCompatActivity() {
    private lateinit var cityRecyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var searchCitySV: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        // Danh sách các tỉnh thành
        val cities = listOf(
            "An Giang", "Bà Rịa - Vũng Tàu", "Bạc Liêu", "Bắc Giang", "Bắc Kạn",
            "Bắc Ninh", "Bến Tre", "Bình Dương", "Bình Định", "Bình Phước",
            "Bình Thuận", "Cà Mau", "Cao Bằng", "Cần Thơ", "Đà Nẵng",
            "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp",
            "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh",
            "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên",
            "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu",  "Lạng Sơn",
            "Lào Cai", "Lâm Đồng", "Long An", "Nam Định", "Nghệ An",
            "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình",
            "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng",
            "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa",
            "Thừa Thiên Huế", "Tiền Giang", "TP. Hồ Chí Minh", "Trà Vinh", "Tuyên Quang",
            "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
        )

        // Cấu hình RecyclerView
        cityRecyclerView = findViewById(R.id.cityRecyclerView)
        cityAdapter = CityAdapter(cities) { selectedCity ->
            val resultsIntent = Intent()
            resultsIntent.putExtra("cityName", selectedCity)
            setResult(RESULT_OK, resultsIntent)
            finish()
        }
        cityRecyclerView.layoutManager = LinearLayoutManager(this)
        cityRecyclerView.adapter = cityAdapter

        findViewById<ImageButton>(R.id.backSearchCityIB).setOnClickListener { finish() }

        // Cấu hình SearchView
        searchCitySV = findViewById(R.id.searchCitySV)
        searchCitySV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredCities = cities.filter { city ->
                    city.contains(p0 ?: "", ignoreCase = true)
                }
                cityAdapter.updateCities(filteredCities)
                return true
            }
        })
    }
}