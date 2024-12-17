    package com.example.project

    import android.os.Bundle
    import android.util.Log
    import android.widget.CheckBox
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore

    class MyCartFragment : AppCompatActivity() {
        private lateinit var cartAdapter: CartAdapter
        private lateinit var checkBoxAll: CheckBox
        private val firestore = FirebaseFirestore.getInstance()
        private val localProducts = mutableListOf<Product>()
        private val checkedProducts = mutableSetOf<Product>()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_my_cart)

            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            checkBoxAll = findViewById(R.id.checkBoxAll)

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
            // Khởi tạo CartAdapter với các callback
            cartAdapter = CartAdapter(
                products = emptyList(),
                userId = userId,
                onIncrease = { product -> updateProductQuantity(product, 1) },
                onDecrease = { product -> updateProductQuantity(product, -1) },
                onRemove = { product -> removeProductFromCart(product) },
                onCheckedChange = { position, isChecked -> handleCheckedChange(position, isChecked) }
            )
            recyclerView.adapter = cartAdapter
            // Load dữ liệu giỏ hàng từ Firebase Firestore
            loadCartData()

            checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
                toggleAllProducts(isChecked)
                // Cập nhật trạng thái của tất cả các sản phẩm trong localProducts
                localProducts.forEach { it.isChecked = isChecked }

                // Cập nhật trạng thái "checked" của toàn bộ sản phẩm
                checkedProducts.clear()
                if (isChecked) {
                    checkedProducts.addAll(localProducts)
                }

                // Gọi Adapter để cập nhật tất cả CheckBox trong RecyclerView
                cartAdapter.setAllChecked(isChecked)

                // Tính lại tổng giá tiền
                updateCheckedTotalPrice()
            }
        }

        private fun handleCheckedChange(position: Int, isChecked: Boolean) {
            // Cập nhật trạng thái của sản phẩm được chọn
            localProducts[position].isChecked = isChecked

            // Kiểm tra nếu tất cả các sản phẩm đã được chọn
            checkBoxAll.isChecked = localProducts.all { it.isChecked }

            // Cập nhật Adapter và tính lại tổng giá
            cartAdapter.updateData(localProducts)
            updateCheckedTotalPrice()

            // Log trạng thái sản phẩm
            Log.d("CartState", "CheckboxAll: ${checkBoxAll.isChecked}")
            localProducts.forEachIndexed { index, product ->
                Log.d("CartState", "Product $index - isChecked: ${product.isChecked}")
            }
        }

        private fun toggleAllProducts(isChecked: Boolean) {
            // Cập nhật trạng thái của tất cả sản phẩm
            localProducts.forEach { it.isChecked = isChecked }

            // Cập nhật Adapter và tổng giá
            cartAdapter.notifyDataSetChanged()
            updateCheckedTotalPrice()
        }

        private fun loadCartData() {
            val userId = "example_user_id" // Thay đổi bằng userId thực tế
            firestore.collection("carts")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener { result ->
                    val updatedProducts = mutableListOf<Product>()
                    for (document in result) {
                        val product = document.toObject(Product::class.java)
                        if (product.id != null && product.price > 0 && product.quantity > 0) {
                            updatedProducts.add(product)
                        }
                    }

                    if (localProducts != updatedProducts) {
                        localProducts.clear()
                        localProducts.addAll(updatedProducts)
                        cartAdapter.updateData(localProducts)
                    }

                    findViewById<TextView>(R.id.productsNum).text = "(${localProducts.size})"
                    updateCheckedTotalPrice()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Hàm cập nhật số lượng sản phẩm
        private fun updateProductQuantity(product: Product, change: Int) {
            val userId = "example_user_id"
            val productRef = firestore.collection("carts").document(userId)
                .collection("products").document(product.id)

            // Cập nhật số lượng cục bộ
            val updatedProducts = localProducts.map { it.copy() }.toMutableList()
            val index = localProducts.indexOfFirst { it.id == product.id }
            if (index != -1) {
                updatedProducts[index].quantity += change
                if (updatedProducts[index].quantity < 1) {
                    updatedProducts.removeAt(index)
                }
                localProducts.clear()
                localProducts.addAll(updatedProducts)

                // Cập nhật Adapter
                cartAdapter.updateData(localProducts)
                updateCheckedTotalPrice() // Tính lại tổng tiền
            }

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val updatedQuantity = snapshot.getLong("quantity")?.toInt()?.plus(change) ?: 0
                if (updatedQuantity > 0) {
                    transaction.update(productRef, "quantity", updatedQuantity)
                } else {
                    transaction.delete(productRef)
                }
            }.addOnSuccessListener {
                updateCheckedTotalPrice()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error updating product: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Hàm xóa sản phẩm khỏi giỏ hàng
        private fun removeProductFromCart(product: Product) {
            val userId = "example_user_id" // Thay đổi bằng userId thực tế
            firestore.collection("carts").document(userId)
                .collection("products").document(product.id)
                .delete()
                .addOnSuccessListener {
                    loadCartData() // Cập nhật lại giỏ hàng sau khi xóa
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        "Error removing product: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        private fun updateTotalPrice() {
            val totalPrice = localProducts.sumOf { it.quantity * it.price }
            findViewById<TextView>(R.id.textViewTotal).text = "Total: ${totalPrice}đ"
        }

        private fun setUpCheckBoxAll() {
            val checkBoxAll = findViewById<CheckBox>(R.id.checkBoxAll)
            checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
                // Cập nhật trạng thái của tất cả sản phẩm
                localProducts.forEach { it.isChecked = isChecked }

                // Cập nhật lại danh sách checkedProducts nếu chọn tất cả
                checkedProducts.clear()
                if (isChecked) {
                    checkedProducts.addAll(localProducts)
                }

                // Cập nhật Adapter
                cartAdapter.updateData(localProducts)

                // Tính lại tổng giá tiền
                updateCheckedTotalPrice()
            }
        }

        private fun updateCheckedTotalPrice() {
            val totalPrice = localProducts.filter { it.isChecked }.sumOf { it.quantity * it.price }
            findViewById<TextView>(R.id.textViewTotal).text = "Total: ${totalPrice}đ"
        }
    }