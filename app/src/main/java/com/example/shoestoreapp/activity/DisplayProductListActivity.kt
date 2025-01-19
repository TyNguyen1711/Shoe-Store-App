package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.BestSellingRepository
import com.example.shoestoreapp.data.repository.ExclusiveOfferRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import kotlinx.coroutines.launch

class DisplayProductListActivity : AppCompatActivity(), ProductItemAdapter.OnProductClickListener {
    private lateinit var productListAdapters: ProductItemAdapter
    private val exclusiveOfferRepository = ExclusiveOfferRepository()
    private val bestSellingRepository = BestSellingRepository()
    private val productListRepository = ProductRepository()
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display_product_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _pageName = intent.getStringExtra("listName")
        val backBtn = findViewById<Button>(R.id.reviewact_backBtn)
        val productListRC = findViewById<RecyclerView>(R.id.productListRV)

        val pageName = findViewById<TextView>(R.id.pageName)

        pageName.text = _pageName.toString().uppercase()
        backBtn!!.setOnClickListener() {
            finish()
        }

        productListAdapters = ProductItemAdapter(productList, this, this, false)
        productListRC.adapter = productListAdapters
        productListRC.layoutManager = GridLayoutManager(this, 2)

        when (_pageName) {
            "All" -> loadAllProducts()
            "Nike" -> loadBrandProducts("Nike")
            "Adidas" -> loadBrandProducts("Adidas")
            "Puma" -> loadBrandProducts("Puma")
            "New Balance" -> loadBrandProducts("New Balance")
            "Rebook" -> loadBrandProducts("Rebook")
            "Lacoste" -> loadBrandProducts("Lacoste")
            "Exclusive Offer" -> loadExclusiveOffer()
            "Best Selling" -> loadBestSelling()
        }
    }

    private fun loadAllProducts() {
        lifecycleScope.launch {
            val result = productListRepository.getAllProducts()
            result.onSuccess { items ->
                productList.clear()
                productList.addAll(items)
                productListAdapters.notifyDataSetChanged()
            }.onFailure { error ->
                Log.e("DisplayProduct", "Failed to fetch products: ${error.message}")
            }
        }
    }

    private fun loadBrandProducts(brand: String) {
        lifecycleScope.launch {
            val result = productListRepository.getProductsByBrand(brand)
            result.onSuccess { items ->
                productList.clear()
                productList.addAll(items)
                productListAdapters.notifyDataSetChanged()
            }.onFailure { error ->
                Log.e("DisplayProduct", "Failed to fetch products: ${error.message}")
            }
        }
    }

    private fun loadExclusiveOffer() {
        lifecycleScope.launch {
            val result = exclusiveOfferRepository.getAllProducts()
            result.onSuccess { items ->
                productList.clear()
                productList.addAll(items)
                productListAdapters.notifyDataSetChanged()
            }.onFailure { error ->
                Log.e("DisplayProduct", "Failed to fetch products: ${error.message}")
            }
        }
    }

    private fun loadBestSelling() {
        lifecycleScope.launch {
            val result = bestSellingRepository.getAllProducts()
            result.onSuccess { items ->
                productList.clear()
                productList.addAll(items)
                productListAdapters.notifyDataSetChanged()
            }.onFailure { error ->
                Log.e("DisplayProduct", "Failed to fetch products: ${error.message}")
            }
        }
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("productId", productId)
        startActivity(intent)
    }

    override fun onMoreButtonClick(listName: String) {
        Toast.makeText(this, "More Button Clicked!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DisplayProductListActivity::class.java)
        intent.putExtra("listName", listName)
        startActivity(intent)
        // Thêm logic khi nhấn "More Button" (ví dụ: chuyển tới danh sách đầy đủ)
    }
}