package com.example.shoestoreapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.adapter.CartAdapter
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkBoxAll: CheckBox
    private val firestore = FirebaseFirestore.getInstance()
    private val localCartItems = mutableListOf<CartItem>()
    private val checkedCartItems = mutableSetOf<CartItem>()
    private val prices = mutableListOf<Double>()
    private val names = mutableListOf<String>()
    private val thumbnail = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho Fragment
        return inflater.inflate(R.layout.activity_my_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        checkBoxAll = view.findViewById(R.id.checkBoxAll)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        // Khởi tạo CartAdapter với các callback
        cartAdapter = CartAdapter(
            products = emptyList(),
            userId = userId,
            onIncrease = { product -> updateCartItemQuantity(product, 1) },
            onDecrease = { product -> updateCartItemQuantity(product, -1) },
            onRemove = { product -> removeCartItemFromCart(product) },
            onCheckedChange = { position, isChecked -> handleCheckedChange(position, isChecked) },
            images = emptyList(),
            prices = emptyList(),
            names = emptyList()
        )
        recyclerView.adapter = cartAdapter

        // Load dữ liệu giỏ hàng từ Firebase Firestore
        loadCartData(view)

        checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
            toggleAllCartItems(isChecked)
            localCartItems.forEach { it.isChecked = isChecked }

            checkedCartItems.clear()
            if (isChecked) {
                checkedCartItems.addAll(localCartItems)
            }

            cartAdapter.setAllChecked(isChecked)
            updateCheckedTotalPrice(view)
        }
        cartAdapter.notifyDataSetChanged()
    }

    private fun handleCheckedChange(position: Int, isChecked: Boolean) {
        localCartItems[position].isChecked = isChecked
        checkBoxAll.isChecked = localCartItems.all { it.isChecked }
        cartAdapter.updateData(localCartItems)
        updateCheckedTotalPrice(requireView())
    }

    private fun toggleAllCartItems(isChecked: Boolean) {
        localCartItems.forEach { it.isChecked = isChecked }
        cartAdapter.notifyDataSetChanged()
        updateCheckedTotalPrice(requireView())
    }

    private fun loadCartData(view: View) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        firestore.collection("carts")
            .document(userId)
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                val updatedCartItems = mutableListOf<CartItem>()
                val productRepository = ProductRepository(firestore) // Tạo một instance của ProductRepository

                // Sử dụng CoroutineScope để xử lý các hàm suspend
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in result) {
                        val cartItem = document.toObject(CartItem::class.java)
                        val productId = cartItem.productId
                        updatedCartItems.add(cartItem)
                        // Gọi getProduct để lấy thông tin sản phẩm
                        val productResult = productRepository.getProduct(productId)
                        productResult.onSuccess { product ->
                            prices.add(product.price)
                            thumbnail.add(product.thumbnail)
                            names.add(product.name)
                            println(product.price)
                        }.onFailure { error ->
                            // Xử lý lỗi nếu không lấy được sản phẩm
                            println("Failed to fetch product: ${error.message}")
                        }
                    }

                    // Chuyển về Main thread để cập nhật UI nếu cần
                    withContext(Dispatchers.Main) {
                        // Xử lý updatedCartItems ở đây (ví dụ: cập nhật giao diện)
                        if (localCartItems != updatedCartItems) {
                            localCartItems.clear()
                            localCartItems.addAll(updatedCartItems)
                            cartAdapter.updateData(localCartItems, thumbnail, names, prices)
                            println(cartAdapter)
                        }
                        view.findViewById<TextView>(R.id.productsNum).text = localCartItems.size.toString()
                        updateCheckedTotalPrice(view)
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading cart: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCartItemQuantity(product: CartItem, change: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        var index:Int = 0
        firestore.collection("carts").document(userId)
            .collection("products").get().addOnSuccessListener { result ->
                for (document in result)
                {
                    val cartItem = document.toObject(CartItem::class.java)
                    val productId = cartItem.productId
                    if (productId == product.productId)
                    {
                        break
                    }
                    index ++
                }
            }

        val updatedCartItems = localCartItems.map { it.copy() }.toMutableList()
        if (index != -1) {
            updatedCartItems[index].quantity += change
            if (updatedCartItems[index].quantity < 1) {
                updatedCartItems.removeAt(index)
            }
            localCartItems.clear()
            localCartItems.addAll(updatedCartItems)
            cartAdapter.updateData(localCartItems)
            updateCheckedTotalPrice(requireView())
        }

        // Query for the document that has the productId field matching the given productId
        firestore.collection("carts").document(userId)
            .collection("products").get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    // If a matching document is found, get the reference to the document
                    val productRef = snapshot.documents[0].reference
                    firestore.runTransaction { transaction ->
                        val productSnapshot = transaction.get(productRef)
                        val updatedQuantity = productSnapshot.getLong("quantity")?.toInt()?.plus(change) ?: 0
                        if (updatedQuantity > 0) {
                            transaction.update(productRef, "quantity", updatedQuantity)
                        } else {
                            transaction.delete(productRef)
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Error updating product: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Product not found in cart.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching product: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeCartItemFromCart(product: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        firestore.collection("carts").document(userId)
            .collection("products").document(product.productId)
            .delete()
            .addOnSuccessListener {
                loadCartData(requireView())
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error removing product: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateCheckedTotalPrice(view: View) {
        val totalPrice = localCartItems.filter { it.isChecked }
            .sumOf { cartItem ->
                val index = localCartItems.indexOf(cartItem)
                val price = prices.getOrElse(index) { 0.0 } // Lấy giá tại index tương ứng
                cartItem.quantity * price
            }
        view.findViewById<TextView>(R.id.textViewTotal).text = "Total: ${totalPrice}đ"
    }
}
