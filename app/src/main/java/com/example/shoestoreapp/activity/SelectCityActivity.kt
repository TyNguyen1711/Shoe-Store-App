package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.shoestoreapp.R
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.classes.ApiService
import com.example.shoestoreapp.data.model.City
import com.example.shoestoreapp.data.model.CityResponse
import com.example.shoestoreapp.data.model.District
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectCityActivity : AppCompatActivity() {
    private lateinit var citySpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var wardSpinner: Spinner
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        citySpinner = findViewById(R.id.citySpinner)
        districtSpinner = findViewById(R.id.districtSpinner)
        wardSpinner = findViewById(R.id.wardSpinner)

        // Setup Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://esgoo.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Lấy dữ liệu thành phố
        fetchCities()

        val confirmBT = findViewById<Button>(R.id.confirmBT)
        confirmBT.setOnClickListener {
            val selectedWard = wardSpinner.selectedItem?.toString() ?: ""
            val selectedDistrict =  districtSpinner.selectedItem?.toString() ?: ""
            val selectedCity = citySpinner.selectedItem?.toString() ?: ""

            if (selectedWard.isNotEmpty() && selectedDistrict.isNotEmpty() && selectedCity.isNotEmpty()) {
                val result = "$selectedWard, $selectedDistrict, $selectedCity"

                // Truyền kết quả về Activity trước đó
                val intent = Intent().apply {
                    putExtra("location_result", result)
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Please select Ward, District, City", Toast.LENGTH_SHORT).show()
            }
        }

        // khi nhấn nút back
        findViewById<ImageButton>(R.id.backSearchCityIB).setOnClickListener{ finish() }
    }

    private fun fetchCities() {
        apiService.getCityData(4, 0).enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                if (response.isSuccessful) {
                    val cityList = response.body()?.data ?: emptyList()
                    val cityNames = cityList.map { it.name }
                    val cityAdapter = ArrayAdapter(this@SelectCityActivity, android.R.layout.simple_spinner_item, cityNames)
                    citySpinner.adapter = cityAdapter

                    citySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedCity = cityList[position]
                            fetchDistricts(selectedCity)
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    })
                } else {
                    Toast.makeText(this@SelectCityActivity, "Failed to load cities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                Toast.makeText(this@SelectCityActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDistricts(city: City) {
        val districtNames = city.data2.map { it.name }
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districtNames)
        districtSpinner.adapter = districtAdapter

        districtSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDistrict = city.data2[position]
                fetchWards(selectedDistrict)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where no district is selected
            }
        })
    }

    private fun fetchWards(district: District) {
        val wardNames = district.data3.map { it.name }
        val wardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wardNames)
        wardSpinner.adapter = wardAdapter
    }
}