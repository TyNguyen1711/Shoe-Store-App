package com.example.shoestoreapp.activity

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.shoestoreapp.R
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import java.util.Locale
import org.apache.commons.text.similarity.LevenshteinDistance
import java.text.Normalizer
import java.util.regex.Pattern

class SelectCityActivity : AppCompatActivity() {
    private lateinit var citySpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var wardSpinner: Spinner
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        showLoading(true)

        citySpinner = findViewById(R.id.citySpinner)
        districtSpinner = findViewById(R.id.districtSpinner)
        wardSpinner = findViewById(R.id.wardSpinner)

        // Setup Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://esgoo.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val location = intent.getStringExtra("location") ?: ""
        Log.d("Location", "Location: $location")

        // Lấy dữ liệu thành phố
        fetchCities {
            if (location.isNotEmpty()) {
                loadData(location)
            }
        }

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

    private fun loadData(location: String) {
        val parts = location.split(",").map { it.trim() }
        if (parts.size < 3) {
            Toast.makeText(this, "Invalid location format", Toast.LENGTH_SHORT).show()
            return
        }
        val city = parts[2]
        val district = parts[1]
        val ward = parts[0]
        // Bắt đầu cập nhật Spinner tuần tự
        setSpinnerSelection(citySpinner, city) {
            // Khi thành phố được chọn, tải dữ liệu quận/huyện
            fetchDistrictsForCity(city) {
                setSpinnerSelection(districtSpinner, district) {
                    // Khi quận/huyện được chọn, tải dữ liệu phường/xã
                    fetchWardsForDistrict(district) {
                        setSpinnerSelection(wardSpinner, ward)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val loadingLayout = findViewById<FrameLayout>(R.id.loadingLayout)
        val progressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)

        if (isLoading) {
            loadingLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            loadingLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    fun onUseLocationClick(view: View) {
        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = com.google.android.gms.location.LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = com.google.android.gms.location.LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // Kiểm tra quyền truy cập vị trí
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            task.addOnSuccessListener {
                // Nếu GPS đã bật, lấy vị trí
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            fetchLocationDetails(latitude, longitude)
                        } else {
                            Toast.makeText(this, "Unable to get location. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            task.addOnFailureListener { e ->
                if (e is com.google.android.gms.common.api.ResolvableApiException) {
                    e.startResolutionForResult(this, 100)
                } else {
                    Toast.makeText(this, "Location services are not available.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Yêu cầu quyền nếu chưa có
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    // Xử lý kết quả trả về từ requestPermissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            // Nếu quyền được cấp, gọi lại phương thức để lấy vị trí
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                onUseLocationClick(View(this))  // Gọi lại phương thức để lấy vị trí
            } else {
                Toast.makeText(this, "Permission denied. Cannot get location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLocationDetails(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.ENGLISH)

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0)

                val city = address.adminArea ?: "Unknown"
                var district = address.subAdminArea ?: address.locality ?: "Unknown"
                val parts = addressText.split(",")

                if (district == "Unknown" && parts.size >= 3) {
                    district = parts[parts.size - 3].trim()
                }

                // Trích xuất phường
                val ward = if (parts.size >= 4) {
                    parts[parts.size - 4].trim()
                } else {
                    address.subLocality ?: "Unknown"
                }

                // Cập nhật Spinner tuần tự
                setSpinnerSelection(citySpinner, city) {
                    // Tải dữ liệu quận/huyện
                    fetchDistrictsForCity(city) {
                        setSpinnerSelection(districtSpinner, district) {
                            // Tải dữ liệu phường/xã
                            fetchWardsForDistrict(district) {
                                setSpinnerSelection(wardSpinner, ward)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Address not found from coordinates", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String, onItemSelected: (() -> Unit)? = null) {
        val adapter = spinner.adapter
        val levenshtein = LevenshteinDistance()
        var bestMatchIndex = -1
        var bestMatchDistance = Int.MAX_VALUE

        for (i in 0 until adapter.count) {
            val item = normalizeName(adapter.getItem(i).toString())
            val distance = levenshtein.apply(item, value)
            if (distance < bestMatchDistance) {
                bestMatchDistance = distance
                bestMatchIndex = i
            }
        }

        if (bestMatchIndex != -1) {
            spinner.setSelection(bestMatchIndex, false) // Đặt giá trị mà không kích hoạt sự kiện
            onItemSelected?.invoke() // Gọi callback sau khi đặt xong
        }
    }

    // Hàm chuẩn hóa tên thành phố
    private fun normalizeName(ward: String): String {
        return ward
            .replace("Phường", "", ignoreCase = true)
            .replace("Xã", "", ignoreCase = true)
            .replace("Thành phố", "")
            .replace("Huyện", "")
            .replace("Thị xã", "")
            .trim()
    }

    private fun fetchCities(onComplete: () -> Unit) {
        showLoading(true)
        apiService.getCityData(4, 0).enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                showLoading(false)
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
                    onComplete()  // Gọi callback sau khi dữ liệu đã tải xong
                } else {
                    Toast.makeText(this@SelectCityActivity, "Failed to load cities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                showLoading(false)
                showErrorDialog { fetchCities(onComplete) } // Gọi lại API khi người dùng nhấn "Reload"
            }
        })
    }

    private fun fetchDistricts(city: City) {
        val districtNames = city.data2.map { it.full_name }
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
        val wardNames = district.data3.map { it.full_name }
        val wardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wardNames)
        wardSpinner.adapter = wardAdapter
    }

    private fun fetchDistrictsForCity(cityName: String, onComplete: () -> Unit) {
        showLoading(true)

        apiService.getCityData(4, 0).enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val cityList = response.body()?.data ?: emptyList()
                    // Chuẩn hóa tên thành phố để so sánh
                    val normalizedCityName = normalizeCityName(cityName)
                    val selectedCity = cityList.find {
                        normalizeCityName(it.full_name).contains(normalizedCityName, ignoreCase = true) ||
                                normalizedCityName.contains(normalizeCityName(it.full_name), ignoreCase = true)
                    }

                    if (selectedCity != null) {
                        val districtNames = selectedCity.data2.map { it.full_name }
                        val districtAdapter = ArrayAdapter(this@SelectCityActivity, android.R.layout.simple_spinner_item, districtNames)
                        districtSpinner.adapter = districtAdapter
                        onComplete() // Hoàn tất và gọi callback
                    } else {
                        Toast.makeText(this@SelectCityActivity, "City not found in API data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SelectCityActivity, "Failed to load districts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@SelectCityActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hàm chuẩn hóa tên thành phố
    private fun normalizeCityName(cityName: String): String {
        return cityName
            .replace("Thành phố", "", ignoreCase = true)
            .replace("Tỉnh", "", ignoreCase = true)
            .trim()
            .let { removeAccents(it) }
    }

    // Hàm loại bỏ dấu tiếng Việt
    private fun removeAccents(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("")
    }

    private fun fetchWardsForDistrict(districtName: String, onComplete: () -> Unit) {
        showLoading(true)
        apiService.getCityData(4, 0).enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val cityList = response.body()?.data ?: emptyList()
                    val normalizedDistrictName = normalizeDistrictName(districtName)
                    val selectedDistrict = cityList.flatMap { it.data2 }.find { normalizeDistrictName(it.full_name).equals(normalizedDistrictName, ignoreCase = true) }
                    if (selectedDistrict != null) {
                        val wardNames = selectedDistrict.data3.map { it.full_name }
                        val wardAdapter = ArrayAdapter(this@SelectCityActivity, android.R.layout.simple_spinner_item, wardNames)
                        wardSpinner.adapter = wardAdapter
                        onComplete() // Hoàn tất và gọi callback
                    } else {
                        Toast.makeText(this@SelectCityActivity, "District not found in API data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SelectCityActivity, "Failed to load wards", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@SelectCityActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Chuẩn hóa tên quận/huyện
    private fun normalizeDistrictName(districtName: String): String {
        return districtName
            .replace("Thị xã", "", ignoreCase = true)
            .replace("Thành phố", "", ignoreCase = true)
            .replace("Huyện", "", ignoreCase = true)
            .replace("District", "", ignoreCase = true) // Loại bỏ từ "District"
            .trim()
            .let { removeAccents(it) } // Loại bỏ dấu tiếng Việt
    }

    private fun showErrorDialog(onRetry: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Data loading error")
            .setMessage("Unable to load data from API. Please check your network connection and try again.")
            .setCancelable(false)
            .setPositiveButton("Reload") { dialog, _ ->
                dialog.dismiss()
                onRetry()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }
}