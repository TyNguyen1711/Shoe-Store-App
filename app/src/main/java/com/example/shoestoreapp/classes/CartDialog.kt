// CartDialog.kt
package com.example.shoestoreapp.classes

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartDialog(
    private val context: Context,
    private val product: Product,
    private val cartRepository: CartRepository
) {
    private lateinit var dialog: Dialog
    private lateinit var sizeButtons: List<TextView>
    private lateinit var quantityEditText: EditText
    private var selectedSize: String? = null

    fun show() {
        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null)
        dialog.setContentView(view)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val gridLayout = view.findViewById<GridLayout>(R.id.sizeGridLayout)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        val decreaseBtn = view.findViewById<Button>(R.id.decreaseBtn)
        val increaseBtn = view.findViewById<Button>(R.id.increaseBtn)
        val addToCartBtn = view.findViewById<Button>(R.id.addToCartBtn)

        setupSizeButtons(gridLayout)
        setupQuantityControls(decreaseBtn, increaseBtn)

        addToCartBtn.setOnClickListener {
            if (selectedSize == null) {
                Toast.makeText(context, "Please select a size", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1
            if (quantity < 1 || quantity > 10) {
                Toast.makeText(context, "Quantity must be between 1 and 10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addToCart(quantity)
        }

        dialog.show()
    }

    private fun setupSizeButtons(gridLayout: GridLayout) {
        val sizes = product.variants.map { it.size.toString() }
        sizeButtons = sizes.map { size ->
            TextView(context).apply {
                text = size
                setBackgroundResource(R.drawable.size_button_background)
                setPadding(24, 24, 24, 24)
                setTextColor(Color.BLACK)
                isClickable = true

                setOnClickListener {
                    sizeButtons.forEach { button ->
                        button.setBackgroundResource(R.drawable.size_button_background)
                        button.setTextColor(Color.BLACK)
                    }

                    setBackgroundResource(R.drawable.size_button_selected_background)
                    setTextColor(Color.WHITE)
                    selectedSize = size
                }
            }
        }

        sizeButtons.forEachIndexed { index, button ->
            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(index % 4)
                rowSpec = GridLayout.spec(index / 4)
                width = GridLayout.LayoutParams.WRAP_CONTENT

                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(8, 8, 8, 8)
            }
            gridLayout.addView(button, params)
        }
    }

    private fun setupQuantityControls(decreaseBtn: Button, increaseBtn: Button) {
        quantityEditText.setText("1")

        decreaseBtn.setOnClickListener {
            val currentQty = quantityEditText.text.toString().toIntOrNull() ?: 1
            if (currentQty > 1) {
                quantityEditText.setText((currentQty - 1).toString())
            }
        }

        increaseBtn.setOnClickListener {
            val currentQty = quantityEditText.text.toString().toIntOrNull() ?: 1
            if (currentQty < 10) {
                quantityEditText.setText((currentQty + 1).toString())
            }
        }
    }

    private fun addToCart(quantity: Int) {
        val cartItem = CartItem(
            productId = product.id,
            size = selectedSize!!,
            quantity = quantity,
            isChecked = false
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = cartRepository.addProductToCart(
                    userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2",
                    product = cartItem
                )

                if (result.isSuccess) {
                    Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}