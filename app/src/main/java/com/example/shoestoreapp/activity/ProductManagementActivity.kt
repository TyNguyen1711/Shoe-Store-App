package com.example.shoestoreapp.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Product


class ProductManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var btnAddProduct: Button
    private lateinit var productAdapter: ProductAdapter

    // Sample product list
    private val products = mutableListOf(
        Product(
            id = "1",
            name = "iPhone 15 Pro",
            thumbnail = "https://example.com/iphone.jpg",
            description = "Latest iPhone with amazing features",
            categoryId = "smartphones",
            brand = "Apple",
            price = 999.99,
            discountPrice = 949.99,
            salePercentage = 5,
            images = listOf("image1.jpg", "image2.jpg"),
            averageRating = 4.5,
            soldCount = 100,
            reviewCount = 50
        ),
        Product(
            id = "2",
            name = "Samsung Galaxy S24",
            thumbnail = "https://example.com/samsung.jpg",
            description = "Premium Android smartphone",
            categoryId = "smartphones",
            brand = "Samsung",
            price = 899.99,
            images = listOf("image1.jpg", "image2.jpg"),
            averageRating = 4.3,
            soldCount = 80,
            reviewCount = 40
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_product)

        // Initialize views
        recyclerView = findViewById(R.id.productRecyclerView)
        searchBar = findViewById(R.id.searchBar)
        btnAddProduct = findViewById(R.id.btnAddProduct)

        setupRecyclerView()
        setupSearchBar()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            products = products.toMutableList(),
            onEditClick = { product -> handleEditProduct(product) },
            onDeleteClick = { product -> handleDeleteProduct(product) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductManagementActivity)
            adapter = productAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@ProductManagementActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupSearchBar() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterProducts(s.toString())
            }
        })
    }

    private fun setupAddButton() {
        btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun filterProducts(query: String) {
        val filteredProducts = products.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true)
        }
        productAdapter.updateProducts(filteredProducts)
    }

    private fun handleEditProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setMessage("Do you want to edit ${product.name}?")
            .setPositiveButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { dialog, _ ->
                val position = products.indexOf(product)
                products.removeAt(position)
                productAdapter.removeProduct(position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAddProductDialog() {
        Toast.makeText(this, "Add Product clicked", Toast.LENGTH_SHORT).show()
    }
}