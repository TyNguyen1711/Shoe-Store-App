package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.AddressAdapter
import com.example.shoestoreapp.data.model.Address

class AddressSelectionActivity : AppCompatActivity() {
    private val userId = "example_userId"
    private var selectedAddressId: String? = null
    private lateinit var addressList: List<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_selection)

        // Tải danh sách địa chỉ
        loadAddressesFromFirestore(userId)

        findViewById<ImageButton>(R.id.backAddressSelectionIB).setOnClickListener {
            finish()
        }
    }

    fun onAddNewAddressClick(view: View) {
        val intent = Intent(this, NewAddressActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Dữ liệu đã được thêm, gọi lại load lại địa chỉ từ Firestore
            loadAddressesFromFirestore(userId)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAddressesFromFirestore(userId)
    }

    private fun loadAddressesFromFirestore(userId: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId).collection("addresses")

        // Lắng nghe sự thay đổi trong Firestore
        addressesRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                showToast("Error loading addresses: ${e.message}")
                return@addSnapshotListener
            }

            val addressList = mutableListOf<Address>()
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                for (document in querySnapshot) {
                    val address = document.toObject(Address::class.java)
                    addressList.add(address)
                }

                // Nếu chỉ có một địa chỉ trong danh sách, đặt nó làm mặc định.
                if (addressList.size == 1) {
                    addressList[0].isDefault = true
                    updateDefaultAddressInFirestore(userId, addressList, addressList[0].id)
                }

                // Hiển thị danh sách địa chỉ trên RecyclerView.
                setupRecyclerView(addressList)
            } else {
                showToast("No addresses found.")
            }
        }
    }

    private fun setupRecyclerView(addressList: List<Address>) {
        val addressRecyclerView: RecyclerView = findViewById(R.id.listProductASLV)
        val adapter = AddressAdapter(
            addressList = addressList,
            onAddressSelected = { selectedAddress ->
                selectedAddressId = selectedAddress.id
                val resultIntent = Intent().apply {
                    putExtra("selectedAddressId", selectedAddressId)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onEditAddress = { addressToEdit ->
                val intent = Intent(this, EditAddressActivity::class.java)
                intent.putExtra("addressId", addressToEdit.id )
                startActivity(intent)
            }
        )
        addressRecyclerView.adapter = adapter
        addressRecyclerView.layoutManager = LinearLayoutManager(this)

        // Đặt địa chỉ mặc định
        val defaultAddress = addressList.find { it.isDefault }
        if (defaultAddress != null) {
            selectedAddressId = defaultAddress.id
            adapter.selectAddress(defaultAddress.id)
        }
    }

    private fun updateDefaultAddressInFirestore(userId: String, addressList: List<Address>, defaultAddressId: String?) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId).collection("addresses")

        addressList.forEach { address ->
            val isDefault = address.id == defaultAddressId
            val addressDocRef = addressesRef.document(address.id)

            // Cập nhật trường "isDefault"
            addressDocRef.update("isDefault", isDefault)
                .addOnFailureListener { e ->
                    showToast("Error updating default address: ${e.message}")
                }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}