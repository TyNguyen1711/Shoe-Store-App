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
import com.example.shoestoreapp.classes.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkBoxAll: CheckBox
    private val firestore = FirebaseFirestore.getInstance()
    private val localProducts = mutableListOf<Product>()
    private val checkedProducts = mutableSetOf<Product>()

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
            onIncrease = { product -> updateProductQuantity(product, 1) },
            onDecrease = { product -> updateProductQuantity(product, -1) },
            onRemove = { product -> removeProductFromCart(product) },
            onCheckedChange = { position, isChecked -> handleCheckedChange(position, isChecked) }
        )
        recyclerView.adapter = cartAdapter

        // Load dữ liệu giỏ hàng từ Firebase Firestore
        loadCartData(view)

        checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
            toggleAllProducts(isChecked)
            localProducts.forEach { it.isChecked = isChecked }

            checkedProducts.clear()
            if (isChecked) {
                checkedProducts.addAll(localProducts)
            }

            cartAdapter.setAllChecked(isChecked)
            updateCheckedTotalPrice(view)
        }
    }

    private fun handleCheckedChange(position: Int, isChecked: Boolean) {
        localProducts[position].isChecked = isChecked
        checkBoxAll.isChecked = localProducts.all { it.isChecked }
        cartAdapter.updateData(localProducts)
        updateCheckedTotalPrice(requireView())
    }

    private fun toggleAllProducts(isChecked: Boolean) {
        localProducts.forEach { it.isChecked = isChecked }
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
                val updatedProducts = mutableListOf<Product>()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    if (product.id != null && product.price!! > 0 && product.quantity > 0) {
                        updatedProducts.add(product)
                    }
                }

                if (localProducts != updatedProducts) {
                    localProducts.clear()
                    localProducts.addAll(updatedProducts)
                    cartAdapter.updateData(localProducts)
                }

                view.findViewById<TextView>(R.id.productsNum).text = "(${localProducts.size})"
                updateCheckedTotalPrice(view)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading cart: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProductQuantity(product: Product, change: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        val productRef = firestore.collection("carts").document(userId)
            .collection("products").document(product.id.toString())

        val updatedProducts = localProducts.map { it.copy() }.toMutableList()
        val index = localProducts.indexOfFirst { it.id == product.id }
        if (index != -1) {
            updatedProducts[index].quantity += change
            if (updatedProducts[index].quantity < 1) {
                updatedProducts.removeAt(index)
            }
            localProducts.clear()
            localProducts.addAll(updatedProducts)
            cartAdapter.updateData(localProducts)
            updateCheckedTotalPrice(requireView())
        }

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            val updatedQuantity = snapshot.getLong("quantity")?.toInt()?.plus(change) ?: 0
            if (updatedQuantity > 0) {
                transaction.update(productRef, "quantity", updatedQuantity)
            } else {
                transaction.delete(productRef)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error updating product: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProductFromCart(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        firestore.collection("carts").document(userId)
            .collection("products").document(product.id.toString())
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
        val totalPrice = localProducts.filter { it.isChecked }.sumOf { it.quantity * it.price!! }
        view.findViewById<TextView>(R.id.textViewTotal).text = "Total: ${totalPrice}đ"
    }
}
