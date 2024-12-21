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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyCartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkBoxAll: CheckBox
    private val firestore = FirebaseFirestore.getInstance()
    private val localCartItems = mutableListOf<CartItem>()
    private val checkedCartItems = mutableSetOf<CartItem>()

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
            onCheckedChange = { position, isChecked -> handleCheckedChange(position, isChecked) }
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
                for (document in result) {
                    val product = document.toObject(CartItem::class.java)
                    if (product.product.id != null && product.product.price!! > 0 && product.quantity > 0) {
                        updatedCartItems.add(product)
                    }
                }

                if (localCartItems != updatedCartItems) {
                    localCartItems.clear()
                    localCartItems.addAll(updatedCartItems)
                    cartAdapter.updateData(localCartItems)
                }

                view.findViewById<TextView>(R.id.productsNum).text = "(${localCartItems.size})"
                updateCheckedTotalPrice(view)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading cart: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCartItemQuantity(product: CartItem, change: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        val productRef = firestore.collection("carts").document(userId)
            .collection("products").document(product.product.id)

        val updatedCartItems = localCartItems.map { it.copy() }.toMutableList()
        val index = localCartItems.indexOfFirst { it.product.id == product.product.id }
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

    private fun removeCartItemFromCart(product: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        firestore.collection("carts").document(userId)
            .collection("products").document(product.product.id)
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
        val totalPrice = localCartItems.filter { it.isChecked }.sumOf { it.quantity * it.product.price}
        view.findViewById<TextView>(R.id.textViewTotal).text = "Total: ${totalPrice}đ"
    }
}
