package com.example.shoestoreapp.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.SearchActivity
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var searchBar: LinearLayout
    private lateinit var autoCompleteSearch: AutoCompleteTextView
    private lateinit var searchBtn: ImageButton
    private val productRepos = ProductRepository()

    // Sample data for products
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.explore, container, false)

        // Initialize views
        textView = view.findViewById(R.id.textView)
        searchBar = view.findViewById(R.id.searchBar)
        autoCompleteSearch = view.findViewById(R.id.autoCompleteSearch)
        searchBtn = view.findViewById(R.id.searchBtn)

        // Ánh xạ các View
        val avatarImageViews = listOf(
            view.findViewById<ImageView>(R.id.avatarTV1),
            view.findViewById<ImageView>(R.id.avatarTV2),
            view.findViewById<ImageView>(R.id.avatarTV3),
            view.findViewById<ImageView>(R.id.avatarTV4),
            view.findViewById<ImageView>(R.id.avatarTV5),
            view.findViewById<ImageView>(R.id.avatarTV6),
            view.findViewById<ImageView>(R.id.avatarTV7),
            view.findViewById<ImageView>(R.id.avatarTV8)
        )

        val fullNameTextViews = listOf(
            view.findViewById<TextView>(R.id.fullNameTV1),
            view.findViewById<TextView>(R.id.fullNameTV2),
            view.findViewById<TextView>(R.id.fullNameTV3),
            view.findViewById<TextView>(R.id.fullNameTV4),
            view.findViewById<TextView>(R.id.fullNameTV5),
            view.findViewById<TextView>(R.id.fullNameTV6),
            view.findViewById<TextView>(R.id.fullNameTV7),
            view.findViewById<TextView>(R.id.fullNameTV8)
        )

        lifecycleScope.launch {
            val result = productRepos.getAllProducts()

            // Sử dụng CoroutineScope để xử lý các hàm suspend
            result.onSuccess { items ->
                productList.clear()
                productList.addAll(items)

                // Cập nhật UI sau khi dữ liệu được tải xong
                for (i in 0..7) {
                    fullNameTextViews[i].text = productList[i].name
                    Glide.with(this@ExploreFragment) // Hoặc context nếu không trong Activity
                        .load(productList[i].thumbnail)
                        .into(avatarImageViews[i]) // Gán vào ImageView
                }
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                println("Failed to fetch exclusive offer items: ${error.message}")
            }
        }

        setupListeners()

        return view
    }




    private fun setupListeners() {
        // Handle search button click
        searchBtn.setOnClickListener {
            val searchText = autoCompleteSearch.text.toString()
            if (searchText.isNotEmpty()) {
                val intent = Intent(requireContext(), SearchActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", searchText)
                }
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
