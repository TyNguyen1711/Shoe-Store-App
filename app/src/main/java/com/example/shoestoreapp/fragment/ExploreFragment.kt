package com.example.shoestoreapp.fragment


import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
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
import android.graphics.Color
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.SearchActivity
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var searchBar: LinearLayout
    private lateinit var autoCompleteSearch: AutoCompleteTextView
    private lateinit var searchBtn: ImageButton

    private val productRepos = ProductRepository()
    private val userRepos = UserRepository()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

    private lateinit var historyView: LinearLayout

    private var searchHistory = mutableListOf<String>()
    private val productList = mutableListOf<Product>()

    // Khai báo ActivityResultLauncher
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Xóa tất cả các item còn lại trong historyView
            historyView.removeAllViews()
            // Logic làm mới dữ liệu trong Fragment
            showShortlist()
        }
    }

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

        historyView = view.findViewById(R.id.historyView)

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
            val user = userRepos.getUser(userId)

            user.onSuccess { userData ->
                searchHistory.addAll(userData.searchHistory)
                showShortlist()
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }

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



    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun startActivityForResult(searchText: String) {
        val intent = Intent(requireContext(), SearchActivity::class.java).apply {
            putExtra("SEARCH_QUERY", searchText)
        }
        activityResultLauncher.launch(intent)
    }

    private fun setupListeners() {
        // Handle search button click
        searchBtn.setOnClickListener {
            val searchText = autoCompleteSearch.text.toString()
            if (searchText.isNotEmpty()) {
                updateHistory(searchText)
                startActivityForResult(searchText)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showShortlist() {
        for (i in 0 until minOf(3,searchHistory.size)) {
            val newItem = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    45.dpToPx()
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(15.dpToPx(), 0, 0, 0)
                text = searchHistory[i]
                textSize = 15f
                setTextColor(Color.BLACK)
                isClickable = true
                isFocusable = true
            }

            // Thêm Divider
            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.dpToPx()
                )
                setBackgroundColor(Color.parseColor("#E0E0E0"))
            }

            newItem.setOnClickListener {
                val text = newItem.text.toString()
                val intent = Intent(requireContext(), SearchActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", text)
                }
                updateHistory(text)
                startActivity(intent)
            }

            // Thêm item và divider vào layout
            historyView.addView(newItem)
            historyView.addView(divider)
        }

        // Xác định nhãn cho phần tử cuối cùng, nếu lịch sử search ít hơn 4 nhãn là Clear History, ngược lại là See All
        var textLabel: String = if (searchHistory.size > 3) "See all" else "Clear History"
        // Nếu người dùng đã có lịch sử search
        if(searchHistory.size != 0){
            val seeAllTV = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    45.dpToPx()
                )
                gravity = Gravity.CENTER
                setPadding(15.dpToPx(), 0, 0, 0)
                text = textLabel
                textSize = 15f
                isClickable = true
                isFocusable = true
            }
            historyView.addView(seeAllTV)
            seeAllTV.setOnClickListener {
                if (textLabel == "Clear History") {
                    // Nếu đã xóa lịch sử, khôi phục lại
                    clearHistory(seeAllTV)
                    textLabel = "See All"
                } else {
                    // Nếu chưa xóa lịch sử, hiển thị tất cả các item
                    showFullList(seeAllTV)
                    textLabel = "Clear History"
                }
            }
        }
        else{
            // Nếu người dùng chưa có lịch sử search
            val trendingSearch = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    45.dpToPx()
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(15.dpToPx(), 0, 0, 0)
                lifecycleScope.launch {
                    val mostFrequentTerm = userRepos.getMostFrequentSearchTerm()
                    text = mostFrequentTerm
                    println("Ming3993: $text")
                }
                textSize = 15f
                isClickable = true
                isFocusable = true
            }

            trendingSearch.setOnClickListener {
                val text = trendingSearch.text.toString()
                val intent = Intent(requireContext(), SearchActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", text)
                }
                updateHistory(text)
                startActivity(intent)
            }
            historyView.addView(trendingSearch)
        }
    }

    private fun showFullList(seeAllTV: TextView) {
        // Thêm các item còn thiếu (4 đến 10) vào lịch sử
        for (i in 3 until minOf(10,searchHistory.size)) {
            val newItem = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    45.dpToPx()
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(15.dpToPx(), 0, 0, 0)
                text = searchHistory[i]
                textSize = 15f
                setTextColor(Color.BLACK)
                isClickable = true
                isFocusable = true
            }

            // Thêm Divider
            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.dpToPx()
                )
                setBackgroundColor(Color.parseColor("#E0E0E0"))
            }

            newItem.setOnClickListener {
                val text = newItem.text.toString()
                val intent = Intent(requireContext(), SearchActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", text)
                }
                updateHistory(text)
                startActivity(intent)
            }

            // Thêm item và divider vào layout
            historyView.addView(newItem)
            historyView.addView(divider)
        }

        // Đổi văn bản nút thành "Clear History"
        seeAllTV.text = "Clear History"
        (seeAllTV.parent as? ViewGroup)?.removeView(seeAllTV)
        historyView.addView(seeAllTV)
    }

    private fun clearHistory(seeAllTV: TextView) {
        // Xóa tất cả các item còn lại trong historyView
        historyView.removeAllViews()

        // Đổi văn bản nút lại thành "See All"
        seeAllTV.text = "See All"

        lifecycleScope.launch {
            val result = userRepos.updateSearchHistory(userId)
            result.onSuccess {
                // Xử lý khi cập nhật thành công
                searchHistory = mutableListOf<String>()
                showShortlist()
                println("Search history updated successfully!")
            }.onFailure { exception ->
                // Xử lý lỗi nếu có
                println("Error updating search history: ${exception.message}")
            }
        }
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