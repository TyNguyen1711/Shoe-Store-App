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
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), ProductItemAdapter.OnProductClickListener {
    private val productRepos = ProductRepository()
    private val userRepos = UserRepository()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

    private var searchHistory = mutableListOf<String>()
    private var resultProducts = mutableListOf<Product>()
    private lateinit var resultAdapter: ProductItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val resultRecyclerView: RecyclerView = findViewById(R.id.resultRecyclerView)
        resultAdapter = ProductItemAdapter(resultProducts, this, this, false) // Truyền `this` làm OnProductClickListener
        resultRecyclerView.adapter = resultAdapter
        resultRecyclerView.layoutManager = GridLayoutManager(this, 2)

        val filterRecyclerView: RecyclerView = findViewById(R.id.filterRecyclerView)

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
                updateHistory(query)
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
            val user = userRepos.getUser(userId)

            user.onSuccess { userData ->
                searchHistory.addAll(userData.searchHistory)
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }

            val result = productRepos.searchProducts(searchQuery)
            Log.d("SEARCH TEXT: ",result.toString())
            result.onSuccess { items ->
                resultProducts.clear()
                resultProducts.addAll(items)
                resultAdapter.notifyDataSetChanged()


            }.onFailure { error ->
                println("Failed to fetch search items: ${error.message}")
            }
        }
    }

    override fun onProductClick(productId: String) {
        Toast.makeText(this, "Clicked Product: $productId", Toast.LENGTH_SHORT).show()
    }

    override fun onMoreButtonClick(listName: String) {
        // Handle the "More" button click event here
        Toast.makeText(this, "More clicked on list: $listName", Toast.LENGTH_SHORT).show()
        // Add your logic here
    }

    private fun updateHistory(element: String) {
        val index = searchHistory.indexOf(element)
        if(index != -1) {
            searchHistory.removeAt(index) // Loại bỏ phần tử tại vị trí index
        }
        searchHistory.add(0, element) // Thêm phần tử vào đầu danh sách

        lifecycleScope.launch {
            val result = userRepos.updateSearchHistory(userId, searchHistory)
            result.onSuccess {
                // Xử lý khi cập nhật thành công
                println("Search history updated successfully!")
            }.onFailure { exception ->
                // Xử lý lỗi nếu có
                println("Error updating search history: ${exception.message}")
            }
        }
    }
}

