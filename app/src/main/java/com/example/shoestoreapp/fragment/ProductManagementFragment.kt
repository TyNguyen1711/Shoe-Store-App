package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.AddProductActivity
import com.example.shoestoreapp.adapter.ProductAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var btnAddProduct: Button
    private lateinit var productAdapter: ProductAdapter
    private lateinit var products: MutableList<Product>

    private val productRepository = ProductRepository()
    // Sample product list


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.productRecyclerView)
        searchBar = view.findViewById(R.id.searchBar)
        btnAddProduct = view.findViewById(R.id.btnAddProduct)

        setupRecyclerView()
        setupSearchBar()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            val result = productRepository.getAllProducts()
            if (result.isSuccess) {
                val products1 = result.getOrNull() ?: emptyList()
                products = products1.toMutableList()
                productAdapter = ProductAdapter(
                    products = products1.toMutableList(),
                    onEditClick = { product -> handleEditProduct(product) },
                    onDeleteClick = { product -> handleDeleteProduct(product) }
                )

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = productAdapter
                    addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            DividerItemDecoration.VERTICAL
                        )
                    )
                }
            } else {
                val error = result.exceptionOrNull()
                Log.e("WishlistFragment", "Error fetching products: $error")
            }
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
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
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
        AlertDialog.Builder(requireContext())
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
        AlertDialog.Builder(requireContext())
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
        Toast.makeText(requireContext(), "Add Product clicked", Toast.LENGTH_SHORT).show()
    }
}
