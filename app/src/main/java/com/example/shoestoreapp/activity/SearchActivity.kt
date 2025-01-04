package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.adapter.WishlistAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), ProductItemAdapter.OnProductClickListener {
    private val productRepos = ProductRepository()
    private var resultProducts = mutableListOf<Product>()
    private lateinit var resultAdapter: ProductItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val resultRecyclerView: RecyclerView = findViewById(R.id.resultRecyclerView)
        resultAdapter = ProductItemAdapter(resultProducts, this, this, false) // Truyền `this` làm OnProductClickListener
        resultRecyclerView.adapter = resultAdapter
        resultRecyclerView.layoutManager = GridLayoutManager(this, 2)

        val resultTV:TextView = findViewById(R.id.resultTV)
        val backBtn: Button = findViewById(R.id.backBtn)
        val searchText: AutoCompleteTextView = findViewById(R.id.autoCompleteSearch)
        val searchBtn: ImageButton = findViewById(R.id.searchBtn)

        // Quay trở về màn hình trước đó
        backBtn.setOnClickListener {
            finish()
        }

        searchBtn.setOnClickListener {
            val query = searchText.text.toString().trim()
            if (query.isNotEmpty()) {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("SEARCH_QUERY", query) // Truyền từ khóa tìm kiếm qua Intent
                startActivity(intent) // Gọi lại chính Activity
                finish() // Kết thúc Activity hiện tại để tránh chồng chất
            } else {
                Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show()
            }
        }


        // Lấy từ khóa tìm kiếm từ Intent
        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""
        searchText.setText(searchQuery)

        lifecycleScope.launch {
            val result = productRepos.searchProducts(searchQuery)
            Log.d("SEARCH TEXT: ",result.toString())
            result.onSuccess { items ->
                resultProducts.clear()
                resultProducts.addAll(items)
                resultAdapter.notifyDataSetChanged()

                if (resultProducts.isEmpty()) {
                    resultTV.text = "No result found for \"$searchQuery\""
                } else {
                    resultTV.text = "${resultProducts.size} search results for \"$searchQuery\""
                }

            }.onFailure { error ->
                println("Failed to fetch search items: ${error.message}")
            }
        }
    }

    override fun onProductClick(productId: String) {
        Toast.makeText(this, "Clicked Product: $productId", Toast.LENGTH_SHORT).show()
    }

    override fun onMoreButtonClick() {
        Toast.makeText(this, "Clicked More Button", Toast.LENGTH_SHORT).show()
    }
}

