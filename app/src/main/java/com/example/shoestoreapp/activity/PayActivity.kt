package com.example.shoestoreapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ProductCheckoutAdapter
import com.example.shoestoreapp.data.model.Address
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.model.Coupon
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.ProductItem
import com.example.shoestoreapp.data.repository.CartRepository
import com.example.shoestoreapp.data.repository.CouponRepository
import com.example.shoestoreapp.data.repository.OrderRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PayActivity : AppCompatActivity() {
    private var userId: String? = "example_user_id"
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
    private lateinit var voucherSelect: TextView
    private lateinit var voucherArea: ConstraintLayout
    private lateinit var orderMain: Button
    private var costDelivery: Double = 0.0
    private var costVoucher: Double = 0.0
    private var totalPayment: Double = 0.0
    private lateinit var cityNameTV: TextView
    private lateinit var addressDetailTV: TextView
    private lateinit var nameOrdererTV: TextView
    private lateinit var sdtPayTV: TextView
    private var selectedAddressId: String? = null // AddressID currently
    private lateinit var couponRepository: CouponRepository
    private var merchandiseSubtotal: Double = 0.0
    private lateinit var messageET: EditText
    private lateinit var paymentMethodRG: RadioGroup
    private var selectedPaymentMethod: String? = null // Lưu phương thức thanh toán được chọn



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        // Nhận danh sách các ID sản phẩm và userId
        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds") ?: emptyList<String>()
        Log.d("First", ArrayList(selectedProductIds).toString())
        val selectedSize = intent.getStringArrayListExtra("selectedSize") ?: emptyList<String>()
        val userId = intent.getStringExtra("userId")
        var code = intent?.getStringExtra("code")
        var discount = intent?.getStringExtra("discount")
        val quantities = intent.getIntegerArrayListExtra("quantities") ?: emptyList()
        val sizes = intent.getStringArrayListExtra("sizes") ?: emptyList()
        val source = intent?.getStringExtra("source")
        val firestore = FirebaseFirestore.getInstance()
        productRepository = ProductRepository(firestore)
        cartRepository = CartRepository(firestore)

        if (selectedProductIds.size == sizes.size && sizes.size == quantities.size) {
            var productIds = mutableListOf<String>()
            for (i in selectedProductIds.indices) {
                val productId = selectedProductIds[i]
                val size = sizes[i]
                val quantity = quantities[i]

                // Tạo CartItem và thêm vào danh sách
                val cartItem = CartItem(
                    productId = productId.toString(),
                    size = size.toString(),
                    quantity = quantity.toInt(),
                    isChecked = true // Đảm bảo isChecked luôn là true
                )
                productIds.add(productId)
                selectedProducts.add(cartItem)
            }
            lifecycleScope.launch {
                loadProductDetails(productIds) // Lấy thông tin sản phẩm cho danh sách productIds
                updateUI(selectedProducts) // Cập nhật UI sau khi dữ liệu được lấy về
                calculatePrices(selectedProducts, 0) // Tính giá tiền sau khi cập nhật sản phẩm
            }
        } else if (selectedProductIds.isNotEmpty() && userId != null) {
            if (discount != null) {
                loadSelectedProducts(selectedProductIds, userId, discount.toInt())
            }
            else {
                loadSelectedProducts(selectedProductIds, userId, 0)
            }
        } else {
            finish()
        }

        loadAddressDefault()
        Log.d("done", "done")


        val backIB = findViewById<ImageButton>(R.id.backPayIB)
        val placeOrderBT = findViewById<Button>(R.id.orderBT)

        costProductsTV = findViewById(R.id.costProductsTV)
        costDeliveryTV = findViewById(R.id.costDeliveryTV)
        voucherTV = findViewById(R.id.voucherTV)
        totalPaymentTV = findViewById(R.id.totalPaymentTV)
        priceOrderTV =  findViewById(R.id.priceOrderTV)
        saveOrderTV = findViewById(R.id.saveOrderTV)
        voucherSelect = findViewById(R.id.labelVoucherTV2)
        messageET = findViewById(R.id.messageInput)
        paymentMethodRG = findViewById(R.id.paymentMethodRG)

        // Address Detail
        cityNameTV = findViewById(R.id.cityNameTV)
        addressDetailTV = findViewById(R.id.addressDetailTV)
        nameOrdererTV = findViewById(R.id.nameOrdererTV)
        sdtPayTV = findViewById(R.id.sdtPayTV)

        paymentMethodRG.setOnCheckedChangeListener { group, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.cashMethodRB -> "cash"
                R.id.creditCardMethodRB -> "credit card"
                else -> null
            }
        }

        // Click Place Order
        placeOrderBT.setOnClickListener {
            if (selectedAddressId == null) {
                Toast.makeText(this, "Please select a shipping address before ordering!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPaymentMethod == null) {
                Toast.makeText(this, "Please select a payment method before placing the order.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true)

            saveOrderToFirestore { isSuccess ->
                showLoading(false)
                if (isSuccess) {
                    showOrderSuccessDialog()
                } else {
                    Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Click Back Button
        backIB.setOnClickListener { finish() }

        if (code != null && discount != null) {
            voucherSelect.text = code + ": " + "SALE OFF " + discount + "%"
        }

        voucherArea = findViewById<ConstraintLayout>(R.id.voucher_section)
        voucherArea.setOnClickListener {
            val intent = Intent(this, CouponCartActivity::class.java).apply {
                putExtra("pay", "pay")
                putExtra("totalPrice", merchandiseSubtotal.toString())
                putStringArrayListExtra("selectedProductIds", ArrayList(selectedProductIds))
            }
            startActivity(intent)
        }

        orderMain = findViewById<Button>(R.id.orderBT)
        couponRepository = CouponRepository()
        orderMain.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (!code.isNullOrEmpty()) {
                        couponRepository.decrementCouponQuantity(code)
                        Toast.makeText(this@PayActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PayActivity, "Invalid coupon code", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PayActivity, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun saveOrderToFirestore(callback: (Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val productsCollection = firestore.collection("products")
        val orderRepository = OrderRepository(firestore)

        // Get currently date
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormatter.format(Date(System.currentTimeMillis()))

        // Chuẩn bị dữ liệu đơn hàng
        val order = Order(
            id = firestore.collection("orders").document().id,
            userId =  userId!!,
            products = selectedProducts.map { cartItem ->
                ProductItem(cartItem.productId, cartItem.quantity, cartItem.size)
            },
            totalPayment = totalPayment,
            recipientName = nameOrdererTV.text.toString(),
            recipientPhone = sdtPayTV.text.toString(),
            recipientAddress = "${addressDetailTV.text}, ${cityNameTV.text}",
            message = messageET.text.toString(),
            paymentMethod = selectedPaymentMethod ?: "",
            orderTime = currentDate,
            status = "pending",
            voucher = costVoucher
        )

        // Kiểm tra và cập nhật stock từng sản phẩm
        val tasks = mutableListOf<Task<Void>>() // Danh sách các tác vụ Firebase
        for (cartItem in selectedProducts) {
            val productRef = productsCollection.document(cartItem.productId)
            val task = productRef.get().continueWithTask { task ->
                if (task.isSuccessful) {
                    val productDoc = task.result
                    if (productDoc != null && productDoc.exists()) {
                        val variants = productDoc.get("variants") as? List<Map<String, Any>> ?: emptyList()

                        // Tìm size cần giảm stock
                        val updatedVariants = variants.map { variant ->
                            if (variant["size"] == cartItem.size) {
                                val currentStock = (variant["stock"] as? Long)?.toInt() ?: 0
                                if (currentStock >= cartItem.quantity) {
                                    variant.toMutableMap().apply {
                                        this["stock"] = currentStock - cartItem.quantity
                                    }
                                } else {
                                    throw Exception("Not enough stock for size ${cartItem.size}")
                                }
                            } else {
                                variant
                            }
                        }
                        // Cập nhật lại variants
                        productRef.update("variants", updatedVariants)
                    } else {
                        throw Exception("Product not found: ${cartItem.productId}")
                    }
                } else {
                    throw task.exception ?: Exception("Failed to fetch product: ${cartItem.productId}")
                }
            }
            tasks.add(task)
        }

        // Chạy tất cả các tác vụ cập nhật stock
        Tasks.whenAll(tasks).addOnCompleteListener { stockUpdateTask ->
            if (stockUpdateTask.isSuccessful) {
                // Lưu đơn hàng vào Firestore
                lifecycleScope.launch {
                    val result = orderRepository.saveOrder(order)
                    result.onSuccess {
                        // Xóa sản phẩm khỏi giỏ hàng sau khi lưu thành công
                        val productIds = selectedProducts.map { it.productId }
                        val sizeList = selectedProducts.map { it.size }

                        val cartResult = cartRepository.removeProductFromCart(userId!!, productIds, sizeList)
                        cartResult.onSuccess { (success, _) ->
                            if (success) {
                                callback(true)
                            }
                        }.onFailure { exception ->
                            Toast.makeText(
                                this@PayActivity,
                                "Error removing products from cart: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            callback(false)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Error updating stock: ${stockUpdateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }
    }

    private fun loadAddressDefault(userId_tmp: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId_tmp).collection("addresses")

        addressesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val address = document.toObject(Address::class.java)
                    if (address.default == true) {
                        updateAddressUI(address)
                        break
                    }
                }
            }
    }

    private fun showOrderSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_success, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val backToHomeBT = dialogView.findViewById<Button>(R.id.backToHomeBT)
        val viewOrderDetailsBT = dialogView.findViewById<Button>(R.id.viewOrderDetailsBT)

        backToHomeBT.setOnClickListener {
            dialog.dismiss()
            // Quay về màn hình chính
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        viewOrderDetailsBT.setOnClickListener {
            dialog.dismiss()
            // Xem chi tiết đơn hàng

        }
    }

    private fun loadAddressDefault() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val addressesRef = db.collection("address").document(userId!!).collection("addresses")

        addressesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val address = document.toObject(Address::class.java)
                    if (address.default == true) {
                        selectedAddressId = address.id
                        updateAddressUI(address)
                        break
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedAddressId = data?.getStringExtra("selectedAddressId")!!
            if (selectedAddressId != null) {
                fetchAddressById(selectedAddressId!!)
            }
        }
    }

    private fun fetchAddressById(addressId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("address")
            .document(userId!!)
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
        intent.putExtra("userId", userId!!)
        intent.putExtra("addressId", selectedAddressId)
        startActivityForResult(intent, 1)
    }

    private fun calculatePrices(selectedProducts: MutableList<CartItem>, discount: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            for (cartItem in selectedProducts) {
                val product = productRepository.getProduct(cartItem.productId)
                product.onSuccess {
                    merchandiseSubtotal += it.price * cartItem.quantity
                }
            }

            withContext(Dispatchers.Main) {
                val savingMoney = merchandiseSubtotal * discount / 100
                val totalPayment = merchandiseSubtotal + costDelivery - savingMoney

                costProductsTV.text = "${String.format("%,.0f", merchandiseSubtotal)}đ"
                costDeliveryTV.text = "${String.format("%,.0f", costDelivery)}đ"
                voucherTV.text = "-${String.format("%,.0f", savingMoney)}đ"
                totalPaymentTV.text = "${String.format("%,.0f", totalPayment)}đ"
                priceOrderTV.text = "${String.format("%,.0f", totalPayment)}đ"
                saveOrderTV.text = "${String.format("%,.0f", savingMoney)}đ"
            }
        }
    }

    private fun loadSelectedProducts(productIds: List<String>,  userId: String, discount: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val cartResult = cartRepository.getCartItems(userId)

            cartResult.onSuccess { cartItems ->
                val cartProductIds = cartItems.map { it.productId }
                validProducts.clear() // Xóa dữ liệu cũ

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
                            Log.e("ProductFetch", "Failed to fetch product $productId: ${error.message}")
                        }
                    }
                }
            }.onFailure { error ->
                Log.e("CartFetch", "Failed to fetch cart items: ${error.message}")
            }

            withContext(Dispatchers.Main) {
                selectedProducts.clear()
                selectedProducts.addAll(validProducts)
                updateUI(selectedProducts)
                calculatePrices(selectedProducts, discount)
            }
        }
    }


    private fun updateUI(selectedProducts: List<CartItem>) {
        val recyclerView = findViewById<RecyclerView>(R.id.listProductPayLV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        Log.d("nnn",selectedProducts.toString())

        val adapter = recyclerView.adapter as? ProductCheckoutAdapter
        if (adapter == null) {
            val newAdapter = ProductCheckoutAdapter(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
            recyclerView.adapter = newAdapter
            newAdapter.updateData(selectedProducts, thumbnail, prices, names)  // Cập nhật dữ liệu
        } else {
            adapter.updateData(selectedProducts, thumbnail, prices, names)  // Cập nhật dữ liệu nếu Adapter đã được gắn
        }
    }

    private suspend fun loadProductDetails(productIds: List<String>) {
        // Lặp qua danh sách productIds để lấy thông tin từng sản phẩm
        for (productId in productIds) {
            try {
                val productResult = productRepository.getProduct(productId) // Gọi repository để lấy sản phẩm
                productResult.onSuccess { product ->
                    prices.add(product.price)
                    thumbnail.add(product.thumbnail)
                    names.add(product.name)
                }
            } catch (error: Exception) {
                Log.e("ProductFetch", "Failed to fetch product $productId: ${error.message}")
            }
        }
    }
}