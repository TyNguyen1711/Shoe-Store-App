package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Address

class NewAddressActivity : AppCompatActivity() {
    private lateinit var cityTV: TextView
    private lateinit var fullNameET: EditText
    private lateinit var phoneNumberET: EditText
    private lateinit var houseNoET: EditText
    private lateinit var isDefault: Switch
    private var cityName: String? = ""
    private var addressId: String = ""
    private val userId = "example_userId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_address)

        cityTV = findViewById(R.id.cityTV)
        fullNameET = findViewById(R.id.fullNameAddressET)
        phoneNumberET = findViewById(R.id.phoneAddressET)
        houseNoET = findViewById(R.id.streetAddressET)
        isDefault = findViewById(R.id.setDefaultSwitch)
        val submitBT = findViewById<Button>(R.id.submitAddressBT)

        isDefault.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isDefault.isChecked = true
            } else {
                isDefault.isChecked = false
            }
        }

        submitBT.setOnClickListener {
            // Tạo ID
            addressId = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("address").document(userId).collection("addresses").document().id

            val address = Address(
                id = addressId,
                fullName = fullNameET.text.toString(),
                phoneNumber = phoneNumberET.text.toString(),
                city = cityName ?: "",
                houseNo = houseNoET.text.toString(),
                isDefault = isDefault.isChecked
            )
            saveAddressToFirestore(userId, address)

            val resultsIntent = Intent()
            resultsIntent.putExtra("id", addressId)
            setResult(RESULT_OK, resultsIntent)
            finish()
        }

        findViewById<ImageButton>(R.id.backNewAddressIB).setOnClickListener { finish() }
    }

    private fun saveAddressToFirestore(userId: String, address: Address) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId).collection("addresses")

        // Kiểm tra nếu địa chỉ mới được đánh dấu là Default, cập nhật các địa chỉ còn lại
        if (address.isDefault) {
            // Lấy tất cả địa chỉ cũ của người dùng
            addressesRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val oldAddress = document.toObject(Address::class.java)
                        // Nếu địa chỉ cũ có isDefault là true, đặt lại là false
                        if (oldAddress.isDefault) {
                            document.reference.update("isDefault", false)
                        }
                    }
                    // Sau khi cập nhật tất cả các địa chỉ cũ, lưu địa chỉ mới
                    saveNewAddress(addressesRef, address)
                }
                .addOnFailureListener { e ->
                    // Hiển thị thông báo lỗi
                    showToast("Error fetching old addresses: ${e.message}")
                }
        } else {
            // Nếu địa chỉ không phải mặc định, chỉ lưu địa chỉ mới
            saveNewAddress(addressesRef, address)
        }
    }

    private fun saveNewAddress(addressesRef: com.google.firebase.firestore.CollectionReference, address: Address) {
        addressesRef.document(addressId).set(address)
            .addOnSuccessListener {
                showToast("Address saved successfully!")
            }
            .addOnFailureListener { e ->
                showToast("Error saving address: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    fun onChooseCityClick(view: View) {
        val intent = Intent(this, SelectCityActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cityName = data?.getStringExtra("cityName")
            cityTV.text = cityName
            cityTV.setTextColor(Color.BLACK)
        }
    }
}