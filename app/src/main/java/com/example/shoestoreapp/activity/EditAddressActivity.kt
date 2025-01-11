package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Address

class EditAddressActivity : AppCompatActivity() {
    private var userId: String? = null
    private lateinit var fullNameET: EditText
    private lateinit var phoneNumberET: EditText
    private lateinit var cityTV: TextView
    private lateinit var streetNoET: EditText
    private lateinit var setDefaultSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_address)

        // Ánh xạ các View
        fullNameET = findViewById(R.id.fullNameAddressET)
        phoneNumberET = findViewById(R.id.phoneAddressET)
        cityTV = findViewById(R.id.cityTV)
        streetNoET = findViewById(R.id.streetAddressET)
        setDefaultSwitch = findViewById(R.id.setDefaultSwitch)
        val deleteBT = findViewById<Button>(R.id.deleteAddressBT)
        val submitBT = findViewById<Button>(R.id.submitAddressBT)
        val backIB = findViewById<ImageButton>(R.id.backEditAddressIB)

        // Nhận dữ liệu từ Intent
        val addressId = intent.getStringExtra("addressId")
        userId = intent.getStringExtra("userId")

        // Tải dữ liệu địa chỉ
        if (addressId != null && userId != null) {
            loadAddress(addressId)
        } else {
            showToast("Missing required data!")
            finish()
        }

        submitBT.setOnClickListener {
            val updatedAddress = Address(
                id = addressId!!,
                fullName = fullNameET.text.toString(),
                houseNo = streetNoET.text.toString(),
                default = setDefaultSwitch.isChecked,
                phoneNumber = phoneNumberET.text.toString(),
                city = cityTV.text.toString()
            )
            updatedAddress(updatedAddress)
        }

        deleteBT.setOnClickListener {
            deleteAddress(addressId)
        }

        setDefaultSwitch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !setDefaultSwitch.isEnabled) {
                showToast("This address is already the default. You cannot change it.")
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        backIB.setOnClickListener { finish() }
    }

    fun onChooseCityClick(view: View) {
        val intent = Intent(this, SelectCityActivity::class.java)
        intent.putExtra("location", cityTV.text.toString())
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val cityName = data?.getStringExtra("location_result")
            cityTV.text = cityName
        }
    }

    private fun updatedAddress(address: Address) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId!!).collection("addresses")

        addressesRef.get()
            .addOnSuccessListener { documents ->
                db.runTransaction { transaction ->
                    if (address.default) {
                        for (document in documents) {
                            val oldAddress = document.toObject(Address::class.java)
                            if (oldAddress.default) {
                                val oldAddressRef = addressesRef.document(document.id)
                                transaction.update(oldAddressRef, "default", false)
                            }
                        }
                    }

                    // Cập nhật địa chỉ hiện tại
                    val addressDocRef = addressesRef.document(address.id)
                    transaction.set(addressDocRef, address)
                }.addOnSuccessListener {
                    showToast("Address updated successfully!")
                    finish()
                }.addOnFailureListener { e ->
                    showToast("Error fetching addresses: ${e.message}")
                }
            }
    }

    private fun deleteAddress(addressId: String?) {
        if (addressId == null) {
            showToast("Address ID is null!")
            return
        }

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressDocRef = db.collection("address").document(userId!!).collection("addresses").document(addressId!!)

        addressDocRef.get()
            .addOnSuccessListener { document ->
                 if (document.exists() && document.getBoolean("default") == true) {
                     showToast("Cannot delete the default address! You should set the default to another address first.")
                 } else {
                     addressDocRef.delete()
                         .addOnSuccessListener {
                             showToast("Address deleted successfully!")
                             finish()
                         }
                         .addOnFailureListener { e ->
                             showToast("Error deleting address: ${e.message}")
                         }
                 }
            }
            .addOnFailureListener { e ->
                showToast("Error retrieving address: ${e.message}")
            }
    }

    private fun loadAddress(addressId: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressDocRef = db.collection("address").document(userId!!).collection("addresses").document(addressId)

        addressDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val address = document.toObject(Address::class.java)
                    if (address != null) {
                        updateAddressUI(address)
                    }
                } else {
                    showToast("Address not found!")
                    finish()
                }
            }
            .addOnFailureListener { e ->
                showToast("Error loading address: ${e.message}")
            }
    }

    private fun updateAddressUI(address: Address) {
        fullNameET.setText(address.fullName)
        phoneNumberET.setText(address.phoneNumber)
        cityTV.setText(address.city)
        streetNoET.setText(address.houseNo)
        setDefaultSwitch.isChecked = address.default

        // Nếu địa chỉ là mặc định, vô hiệu hóa Switch
        setDefaultSwitch.isEnabled = !address.default
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}