package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.AddressAdapter
import com.example.shoestoreapp.data.model.Address

class AddressSelectionActivity : AppCompatActivity() {
    private var userId: String? = null
    private var selectedAddressId: String? = null
    private var currentAddressId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_selection)

        // Tải danh sách địa chỉ
        userId = intent.getStringExtra("userId")
        currentAddressId = intent.getStringExtra("addressId")
        loadAddressesFromFirestore(userId!!)

        findViewById<ImageButton>(R.id.backAddressSelectionIB).setOnClickListener {
            finish()
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

    fun onAddNewAddressClick(view: View) {
        val intent = Intent(this, NewAddressActivity::class.java)
        intent.putExtra("userId", userId)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            // Hiển thị loading khi tải lại dữ liệu
            showLoading(true)
            loadAddressesFromFirestore(userId!!)
        }
    }

    override fun onResume() {
        super.onResume()
        showLoading(true)
        loadAddressesFromFirestore(userId!!)
    }

    private fun loadAddressesFromFirestore(userId: String) {
        showLoading(true) // Hiển thị loading trước khi bắt đầu tải dữ liệu

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId).collection("addresses")

        // Lắng nghe sự thay đổi trong Firestore
        addressesRef.addSnapshotListener { querySnapshot, e ->
            showLoading(false) // Ẩn loading sau khi hoàn tất tải dữ liệu

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
                    addressList[0].default = true
                    updateDefaultAddressInFirestore(userId, addressList, addressList[0].id)
                }

                // Hiển thị danh sách địa chỉ trên RecyclerView.
                setupRecyclerView(addressList)
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
                intent.putExtra("userId", userId)
                intent.putExtra("addressId", addressToEdit.id)
                startActivityForResult(intent, 2)
            }
        )
        addressRecyclerView.adapter = adapter
        addressRecyclerView.layoutManager = LinearLayoutManager(this)

        // Đặt địa chỉ mặc định
        if (currentAddressId != null) {
            adapter.selectAddress(currentAddressId!!)
        } else {
            val defaultAddress = addressList.find { it.default }
            if (defaultAddress != null) {
                selectedAddressId = defaultAddress.id
                adapter.selectAddress(defaultAddress.id)
            }
        }
    }

    private fun updateDefaultAddressInFirestore(userId: String, addressList: List<Address>, defaultAddressId: String?) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId).collection("addresses")

        addressList.forEach { address ->
            val isDefault = address.id == defaultAddressId
            val addressDocRef = addressesRef.document(address.id)

            // Cập nhật trường "isDefault"
            addressDocRef.update("default", isDefault)
                .addOnFailureListener { e ->
                    showToast("Error updating default address: ${e.message}")
                }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}