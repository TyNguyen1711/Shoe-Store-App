package com.example.shoestoreapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.FilterItemAdapter
import com.example.shoestoreapp.adapter.ProductItemAdapter
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
    private var savedProducts = mutableListOf<Product>()
    private lateinit var resultAdapter: ProductItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var isBrand = true

        var positionRating = RecyclerView.NO_POSITION
        var positionBrand = RecyclerView.NO_POSITION

        val brandList = mutableListOf<String>()
        val ratingList = mutableListOf("5 stars", "From 4 stars", "From 3 stars", "From 2 stars", "From 1 star")
        val tmpList = mutableListOf<String>()

        val resultRecyclerView: RecyclerView = findViewById(R.id.resultRecyclerView)
        resultAdapter = ProductItemAdapter(resultProducts, this, this, false) // Truyền `this` làm OnProductClickListener
        resultRecyclerView.adapter = resultAdapter
        resultRecyclerView.layoutManager = GridLayoutManager(this, 2)

        val filterRecyclerView: RecyclerView = findViewById(R.id.filterRecyclerView)
        filterRecyclerView.visibility = View.GONE
        filterRecyclerView.layoutManager = LinearLayoutManager(this)
        val filterItemAdapter = FilterItemAdapter(tmpList){position ->
            if(isBrand) {
                positionBrand = position
            }
            else {
                positionRating = position
            }
        }
        filterRecyclerView.adapter = filterItemAdapter

        val divider = DividerItemDecoration(this, RecyclerView.VERTICAL)
        filterRecyclerView.addItemDecoration(divider)

        val backBtn: Button = findViewById(R.id.backBtn)
        val searchText: AutoCompleteTextView = findViewById(R.id.autoCompleteSearch)
        val searchBtn: ImageButton = findViewById(R.id.searchBtn)

        val priceFilter: TextView = findViewById(R.id.filterPrice)
        val bestSellerFilter: TextView = findViewById(R.id.filterBestSeller)
        val saleFilter: TextView = findViewById(R.id.filterSale)
        val ratingFilter: TextView = findViewById(R.id.filterRating)
        val brandFilter: TextView = findViewById(R.id.filterBrand)

        val underlinePrice: View = findViewById(R.id.underlinePrice)
        val underlineBestSeller: View = findViewById(R.id.underlineBestSeller)
        val underlineSale: View = findViewById(R.id.underlineSale)
        val underlineBrand: View = findViewById(R.id.underlineBrand)
        val underlineRating: View = findViewById(R.id.underlineRating)

        val filterSettings: LinearLayout = findViewById(R.id.filterSettings)
        val applyBtn: Button = findViewById(R.id.ApplyBtn)
        val resetBtn: Button = findViewById(R.id.ResetBtn)
        filterSettings.visibility = View.GONE

        var topPrice = true
        var topSale = true
        var filterRating = true
        var filterBrand = true
        var filter = ""

        applyBtn.setOnClickListener{
            resultProducts.clear()
            resultProducts.addAll(savedProducts)
            if(filter == "price")
            {
                if(!topPrice){
                    resultProducts.sortByDescending { if (it.discountPrice != null) it.discountPrice else it.price}
                }
                else {
                    resultProducts.sortBy { if (it.discountPrice != null) it.discountPrice else it.price}
                }
            }
            if (filter == "sale")
            {
                if (!topSale) {
                    resultProducts.sortByDescending { it.salePercentage }
                } else {
                    resultProducts.sortBy { it.salePercentage }
                }
            }
            if (filter == "best seller") {
                resultProducts.sortByDescending { it.soldCount }
            }

            filterRating = true
            filterBrand = true
            if (positionBrand != RecyclerView.NO_POSITION) {
                brandFilter.text = brandList[positionBrand]
                resultProducts.retainAll { it.brand == brandList[positionBrand] }

                brandFilter.setTextColor(Color.parseColor("#FF0000"))
                underlineBrand.setBackgroundColor(Color.parseColor("#FF0000"))  // Màu đỏ
            }
            else {
                brandFilter.text = "Brand ▾"
                brandFilter.setTextColor(Color.parseColor("#CCCCCC"))
                underlineBrand.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            if (positionRating != RecyclerView.NO_POSITION) {
                ratingFilter.text = ratingList[positionRating]
                resultProducts.retainAll { it.averageRating >= (5 - positionRating) }

                ratingFilter.setTextColor(Color.parseColor("#FF0000"))
                underlineRating.setBackgroundColor(Color.parseColor("#FF0000"))  // Màu đỏ
            }
            else{
                ratingFilter.text = "Rating ▾"
                ratingFilter.setTextColor(Color.parseColor("#CCCCCC"))
                underlineRating.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            resultAdapter.notifyDataSetChanged()
            resultRecyclerView.visibility = View.VISIBLE
            filterRecyclerView.visibility = View.GONE
            filterSettings.visibility = View.GONE
        }

        resetBtn.setOnClickListener{
            if(isBrand) {
                positionBrand = RecyclerView.NO_POSITION
            }
            else {
                positionRating = RecyclerView.NO_POSITION
            }
            filterItemAdapter.setPosition()
        }

        ratingFilter.setOnClickListener{
            isBrand = false
            filterItemAdapter.updateItems(ratingList)
            filterItemAdapter.setPosition(positionRating)

            if(filterRating) {
                resultRecyclerView.visibility = View.GONE
                filterRecyclerView.visibility = View.VISIBLE
                filterSettings.visibility = View.VISIBLE
                filterRating = false
                filterBrand = true
            }
            else
            {
                resultRecyclerView.visibility = View.VISIBLE
                filterRecyclerView.visibility = View.GONE
                filterSettings.visibility = View.GONE
                filterRating = true
            }
        }

        brandFilter.setOnClickListener {
            isBrand = true
            filterItemAdapter.updateItems(brandList)
            filterItemAdapter.setPosition(positionBrand)

            if (filterBrand) {
                resultRecyclerView.visibility = View.GONE
                filterRecyclerView.visibility = View.VISIBLE
                filterSettings.visibility = View.VISIBLE
                filterBrand = false
                filterRating = true
            } else {
                resultRecyclerView.visibility = View.VISIBLE
                filterRecyclerView.visibility = View.GONE
                filterSettings.visibility = View.GONE
                filterBrand = true
            }
        }


        priceFilter.setOnClickListener{
            filter = "price"
            // reset best seller TV
            bestSellerFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlineBestSeller.setBackgroundColor(Color.parseColor("#CCCCCC"))

            // reset sale TV
            topSale = true
            saleFilter.text="Sale ⇅"
            saleFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlineSale.setBackgroundColor(Color.parseColor("#CCCCCC"))

            underlinePrice.setBackgroundColor(Color.parseColor("#FF0000"))  // Màu đỏ
            priceFilter.setTextColor(Color.parseColor("#FF0000")) // Màu đỏ
            if(topPrice){
                topPrice = false
                priceFilter.text="Price ↓"
                resultProducts.sortByDescending { if (it.discountPrice != null) it.discountPrice else it.price}
            }
            else
            {
                topPrice = true
                priceFilter.text="Price ↑"
                resultProducts.sortBy { if (it.discountPrice != null) it.discountPrice else it.price}
            }
            resultAdapter.notifyDataSetChanged()
        }

        bestSellerFilter.setOnClickListener{
            filter = "best seller"
            // reset price TV
            topPrice = true
            priceFilter.text="Price ⇅"
            priceFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlinePrice.setBackgroundColor(Color.parseColor("#CCCCCC"))

            // reset sale TV
            topSale = true
            saleFilter.text="Sale ⇅"
            saleFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlineSale.setBackgroundColor(Color.parseColor("#CCCCCC"))

            underlineBestSeller.setBackgroundColor(Color.parseColor("#FF0000"))  // Màu đỏ
            bestSellerFilter.setTextColor(Color.parseColor("#FF0000")) // Màu đỏ
            resultProducts.sortByDescending { it.soldCount }
            resultAdapter.notifyDataSetChanged()
        }

        saleFilter.setOnClickListener {
            filter = "sale"
            // reset best seller TV
            bestSellerFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlineBestSeller.setBackgroundColor(Color.parseColor("#CCCCCC"))

            // reset price TV
            topPrice = true
            priceFilter.text="Price ⇅"
            priceFilter.setTextColor(Color.parseColor("#CCCCCC"))
            underlinePrice.setBackgroundColor(Color.parseColor("#CCCCCC"))

            underlineSale.setBackgroundColor(Color.parseColor("#FF0000"))  // Màu đỏ
            saleFilter.setTextColor(Color.parseColor("#FF0000")) // Màu đỏ
            if (topSale) {
                topSale = false
                saleFilter.text = "Sale ↓"
                resultProducts.sortByDescending { it.salePercentage }
            } else {
                topSale = true
                saleFilter.text = "Sale ↑"
                resultProducts.sortBy { it.salePercentage }
            }
            resultAdapter.notifyDataSetChanged()
        }



        // Quay trở về màn hình trước đó
        backBtn.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish() // Hoặc thực hiện logic kết thúc Activity
        }

        searchBtn.setOnClickListener {
            val query = searchText.text.toString().trim()
            if (query.isNotEmpty()) {
                updateHistory(query)
                startActivityForResultWithRecursion(query) // Gọi lại chính Activity
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
            result.onSuccess { items ->
                resultProducts.clear()
                savedProducts.clear()
                savedProducts.addAll(items)
                resultProducts.addAll(items)
                brandList.addAll(resultProducts.map { it.brand }.distinct())
                resultAdapter.notifyDataSetChanged()

            }.onFailure { error ->
                println("Failed to fetch search items: ${error.message}")
            }
        }
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(this@SearchActivity, ProductDetailActivity::class.java)
        intent.putExtra("productId", productId)
        startActivity(intent)
    }

    override fun onMoreButtonClick(listName: String) {
        // Handle the "More" button click event here
        Toast.makeText(this, "More clicked on list: $listName", Toast.LENGTH_SHORT).show()
        // Add your logic here
    }

    private fun startActivityForResultWithRecursion(query: String) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("SEARCH_QUERY", query) // Truyền từ khóa tìm kiếm qua Intent
        startActivityForResult(intent, 1)  // Sử dụng một request code bất kỳ
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

