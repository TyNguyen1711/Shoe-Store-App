package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ProductCheckoutAdapter
import com.example.shoestoreapp.data.model.Address
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.repository.CartRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayActivity : AppCompatActivity() {
    private val userId_tmp = "example_userId"
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private val selectedProducts = mutableListOf<CartItem>()
    private val validProducts = mutableListOf<CartItem>()
    private val prices = mutableListOf<Double>()
    private val names = mutableListOf<String>()
    private val thumbnail = mutableListOf<String>()
    private lateinit var costProductsTV: TextView
    private lateinit var costDeliveryTV: TextView
    private lateinit var voucherTV: TextView
    private lateinit var totalPaymentTV: TextView
    private lateinit var priceOrderTV: TextView
    private lateinit var saveOrderTV: TextView
    private var costDelivery: Double = 0.0
    private var costVoucher: Double = 0.0
    private lateinit var cityNameTV: TextView
    private lateinit var addressDetailTV: TextView
    private lateinit var nameOrdererTV: TextView
    private lateinit var sdtPayTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        // Nhận danh sách các ID sản phẩm và userId
        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: emptyList<String>()
        val userId = intent.getStringExtra("userId")

        if (selectedProductIds.isNotEmpty() && userId != null) {
            val firestore = FirebaseFirestore.getInstance()
            productRepository = ProductRepository(firestore)
            cartRepository = CartRepository(firestore)
            loadSelectedProducts(selectedProductIds, userId)
        } else {
            finish()
        }

        loadAddressDefault(userId_tmp)

        val backIB = findViewById<ImageButton>(R.id.backPayIB)

        costProductsTV = findViewById(R.id.costProductsTV)
        costDeliveryTV = findViewById(R.id.costDeliveryTV)
        voucherTV = findViewById(R.id.voucherTV)
        totalPaymentTV = findViewById(R.id.totalPaymentTV)
        priceOrderTV =  findViewById(R.id.priceOrderTV)
        saveOrderTV = findViewById(R.id.saveOrderTV)

        // Address Detail
        cityNameTV = findViewById(R.id.cityNameTV)
        addressDetailTV = findViewById(R.id.addressDetailTV)
        nameOrdererTV = findViewById(R.id.nameOrdererTV)
        sdtPayTV = findViewById(R.id.sdtPayTV)


        backIB.setOnClickListener { finish() }
    }

    private fun loadAddressDefault(userId_tmp: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId_tmp).collection("addresses")

        addressesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val address = document.toObject(Address::class.java)
                    if (address.isDefault == true) {
                        updateAddressUI(address)
                        break
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedAddressId = data?.getStringExtra("selectedAddressId")
            if (selectedAddressId != null) {
                fetchAddressById(selectedAddressId)
            }
        }
    }

    private fun fetchAddressById(addressId: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = "example_userId"
        db.collection("address")
            .document(userId)
            .collection("addresses")
            .document(addressId)
            .get()
            .addOnSuccessListener { document ->
                val address = document.toObject(Address::class.java)
                if (address != null) {
                    updateAddressUI(address)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching address: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAddressUI(address: Address) {
        cityNameTV.text = address.city
        addressDetailTV.text = address.houseNo
        nameOrdererTV.text = address.fullName
        sdtPayTV.text = address.phoneNumber
    }

    fun onAddressSectionClick(view: View) {
        val intent = Intent(this, AddressSelectionActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun calculatePrices(selectedProducts: MutableList<CartItem>) {
        var merchandiseSubtotal = 0.0
        CoroutineScope(Dispatchers.IO).launch {
            for (cartItem in selectedProducts) {
                val product = productRepository.getProduct(cartItem.productId)
                product.onSuccess {
                    merchandiseSubtotal += it.price * cartItem.quantity
                }
            }

            withContext(Dispatchers.Main) {
                val totalPayment = merchandiseSubtotal + costDelivery - costVoucher

                costProductsTV.text = "${String.format("%,.0f", merchandiseSubtotal)}đ"
                costDeliveryTV.text = "${String.format("%,.0f", costDelivery)}đ"
                voucherTV.text = "-${String.format("%,.0f", costVoucher)}đ"
                totalPaymentTV.text = "${String.format("%,.0f", totalPayment)}đ"
                priceOrderTV.text = "${String.format("%,.0f", totalPayment)}đ"
                saveOrderTV.text = "${String.format("%,.0f", costVoucher)}đ"
            }
        }
    }

    private fun loadSelectedProducts(productIds: List<String>, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Lấy danh sách sản phẩm trong giỏ hàng của người dùng
            val cartResult = cartRepository.getCartItems(userId)

            cartResult.onSuccess { cartItems ->
                val cartProductIds = cartItems.map { it.productId } // Lấy danh sách productId trong giỏ hàng
                for (cartItem in cartItems) {
                    if (productIds.contains(cartItem.productId)) {
                        validProducts.add(cartItem)
                    }
                }

                for (productId in productIds) {
                    if (cartProductIds.contains(productId)) {
                        val productResult = productRepository.getProduct(productId)
                        productResult.onSuccess { product ->
                            prices.add(product.price)
                            thumbnail.add(product.thumbnail)
                            names.add(product.name)
                        }.onFailure { error ->
                            println("Failed to fetch product $productId: ${error.message}")
                        }
                    }
                }
            }.onFailure { error ->
                println("Failed to fetch cart items: ${error.message}")
            }

            // Chuyển về Main thread để cập nhật giao diện
            withContext(Dispatchers.Main) {
                if (validProducts.isNotEmpty()) {
                    selectedProducts.clear()
                    selectedProducts.addAll(validProducts)
                    updateUI(selectedProducts)
                    calculatePrices(selectedProducts)
                } else {
                    Toast.makeText(this@PayActivity, "No valid products found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(selectedProducts: List<CartItem>) {
        val recyclerView = findViewById<RecyclerView>(R.id.listProductPayLV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = ProductCheckoutAdapter(selectedProducts, thumbnail, prices, names)
        recyclerView.adapter = adapter
    }
}