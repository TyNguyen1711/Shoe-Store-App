

package com.example.shoestoreapp.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.WishlistAdapter
import com.example.shoestoreapp.classes.Product

class WishlistActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var productList: ArrayList<Product>
    private lateinit var filterTextViews: List<TextView>
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: Button
    private var isSearchVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wishlist)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        productList = ArrayList()
        productList.add(
            Product(
                id = 1,
                name = "Nike Air Max 270",
                price = 812000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-01-800x800.jpg",
                rating = 4.8f,
                soldCount = 805,
                salePercentage = "20%",
                category = "Nike"
            )
        )

        productList.add(
            Product(
                id = 2,
                name = "Adidas Ultraboost Light",
                price = 3500000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-01-800x800.jpg",
                rating = 4.7f,
                soldCount = 345,
                salePercentage = "20%",
                category = "Adidas"
            )
        )


        productList.add(
            Product(
                id = 3,
                name = "Nike Air Zoom Pegasus 40",
                price = 3200000.0,
                imageUrl = "https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2022/12/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-63a95bbc52f23-26122022153052.jpg",
                rating = 4.6f,
                soldCount = 512,
                salePercentage = "15%",
                category = "Nike"
            )
        )

        productList.add(
            Product(
                id = 4,
                name = "Adidas EQ21 Nữ - Trắng Tím",
                price = 950000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
                rating = 4.5f,
                soldCount = 320,
                salePercentage = "25%",
                category = "Adidas"
            )
        )

        productList.add(
            Product(
                id = 5,
                name = "Giày Nike Air Max SC Nữ - Trắng Tím",
                price = 1120000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
                rating = 4.6f,
                soldCount = 480,
                salePercentage = "15%",
                category = "Nike"
            )
        )
        productList.add(
            Product(
                id = 6,
                name = "Giày Puma Rebound v6 Low Nam - Đen Trắng",
                price = 1800000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-01-800x800.jpg",
                rating = 4.9f,
                soldCount = 980,
                salePercentage = "30%",
                category = "Puma"
            )
        )

        productList.add(
            Product(
                id = 7,
                name = "Giày Thể Thao Adidas NMD_R1 Shoes GX9525 Màu Trắng",
                price = 1350000.0,
                imageUrl = "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6775838-11032024160959.jpg",
                rating = 4.8f,
                soldCount = 450,
                salePercentage = "20%",
                category = "Puma"
            )
        )

        productList.add(
            Product(
                id = 8,
                name = "Giày Lacoste Carnaby Pro CGR 124 Nam - Trắng Xanh",
                price = 950000.0,
                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-carnaby-pro-cgr-124-trang-xanh-01-600x315w.jpg",
                rating = 4.7f,
                soldCount = 670,
                salePercentage = "10%",
                category = "Lacoste"
            )
        )
        wishlistAdapter = WishlistAdapter(this, productList)
        recyclerView.adapter = wishlistAdapter

        filterTextViews = listOf(
            findViewById(R.id.filter_all),
            findViewById(R.id.filter_Nike),
            findViewById(R.id.filter_Adidas),
            findViewById(R.id.filter_Puma),
            findViewById(R.id.filter_NewBalance),
            findViewById(R.id.filter_Rebook),
            findViewById(R.id.filter_Lacoste),
        )

        searchEditText = findViewById(R.id.searchEditText)
        searchBtn = findViewById(R.id.searchBtn)

        setupFilterClickListeners()
        setupSearchFunctionality()
    }

    private fun setupSearchFunctionality() {
        searchBtn.setOnClickListener {
            isSearchVisible = !isSearchVisible
            searchEditText.visibility = if (isSearchVisible) View.VISIBLE else View.GONE

            if (!isSearchVisible) {
                searchEditText.text.clear()
                wishlistAdapter.filterProducts(getCurrentSelectedFilter())
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wishlistAdapter.filterProductsBySearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getCurrentSelectedFilter(): String {
        return when (filterTextViews.indexOfFirst { it.currentTextColor == Color.BLUE }) {
            0 -> "All"
            1 -> "Nike"
            2 -> "Adidas"
            3 -> "Puma"
            4 -> "NewBalance"
            5 -> "Rebook"
            6 -> "Lacoste"
            else -> "All"
        }
    }

    private fun setupFilterClickListeners() {
        setSelectedFilter(filterTextViews[0])

        filterTextViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                filterTextViews.forEach { it.setTextColor(Color.BLACK) }

                textView.setTextColor(Color.BLUE)

                if (isSearchVisible) {
                    searchEditText.text.clear()
                }

                when (index) {
                    0 -> wishlistAdapter.filterProducts("All")
                    1 -> wishlistAdapter.filterProducts("Nike")
                    2 -> wishlistAdapter.filterProducts("Adidas")
                    3 -> wishlistAdapter.filterProducts("Puma")
                    4 -> wishlistAdapter.filterProducts("NewBalance")
                    5 -> wishlistAdapter.filterProducts("Rebook")
                    6 -> wishlistAdapter.filterProducts("Lacoste")
                }
            }
        }
    }

    private fun setSelectedFilter(textView: TextView) {
        filterTextViews.forEach { it.setTextColor(Color.BLACK) }
        textView.setTextColor(Color.BLUE)
    }
}





//package com.example.shoestoreapp.activity
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.shoestoreapp.R
//import com.example.shoestoreapp.classes.WishlistAdapter
//import com.example.shoestoreapp.classes.Product
//
//class WishlistActivity : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var wishlistAdapter: WishlistAdapter
//    private lateinit var productList: ArrayList<Product>
//    private lateinit var filterTextViews: List<TextView>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_wishlist)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//
//        productList = ArrayList()
//        productList.add(
//            Product(
//                id = 1,
//                name = "Nike Air Max 270",
//                price = 812000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-01-800x800.jpg",
//                rating = 4.8f,
//                soldCount = 805,
//                salePercentage = "20%",
//                category = "Nike"
//            )
//        )
//
//        productList.add(
//            Product(
//                id = 2,
//                name = "Adidas Ultraboost Light",
//                price = 3500000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-01-800x800.jpg",
//                rating = 4.7f,
//                soldCount = 345,
//                salePercentage = "20%",
//                category = "Adidas"
//            )
//        )
//
//
//        productList.add(
//            Product(
//                id = 3,
//                name = "Nike Air Zoom Pegasus 40",
//                price = 3200000.0,
//                imageUrl = "https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2022/12/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-63a95bbc52f23-26122022153052.jpg",
//                rating = 4.6f,
//                soldCount = 512,
//                salePercentage = "15%",
//                category = "Nike"
//            )
//        )
//
//        productList.add(
//            Product(
//                id = 4,
//                name = "Adidas EQ21 Nữ - Trắng Tím",
//                price = 950000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
//                rating = 4.5f,
//                soldCount = 320,
//                salePercentage = "25%",
//                category = "Adidas"
//            )
//        )
//
//        productList.add(
//            Product(
//                id = 5,
//                name = "Giày Nike Air Max SC Nữ - Trắng Tím",
//                price = 1120000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
//                rating = 4.6f,
//                soldCount = 480,
//                salePercentage = "15%",
//                category = "Nike"
//            )
//        )
//        productList.add(
//            Product(
//                id = 6,
//                name = "Giày Puma Rebound v6 Low Nam - Đen Trắng",
//                price = 1800000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-01-800x800.jpg",
//                rating = 4.9f,
//                soldCount = 980,
//                salePercentage = "30%",
//                category = "Puma"
//            )
//        )
//
//        productList.add(
//            Product(
//                id = 7,
//                name = "Giày Thể Thao Adidas NMD_R1 Shoes GX9525 Màu Trắng",
//                price = 1350000.0,
//                imageUrl = "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6775838-11032024160959.jpg",
//                rating = 4.8f,
//                soldCount = 450,
//                salePercentage = "20%",
//                category = "Puma"
//            )
//        )
//
//        productList.add(
//            Product(
//                id = 8,
//                name = "Giày Lacoste Carnaby Pro CGR 124 Nam - Trắng Xanh",
//                price = 950000.0,
//                imageUrl = "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-carnaby-pro-cgr-124-trang-xanh-01-600x315w.jpg",
//                rating = 4.7f,
//                soldCount = 670,
//                salePercentage = "10%",
//                category = "Lacoste"
//            )
//        )
//        wishlistAdapter = WishlistAdapter(this, productList)
//        recyclerView.adapter = wishlistAdapter
//
//        filterTextViews = listOf(
//            findViewById(R.id.filter_all),
//            findViewById(R.id.filter_Nike),
//            findViewById(R.id.filter_Adidas),
//            findViewById(R.id.filter_Puma),
//            findViewById(R.id.filter_NewBalance),
//            findViewById(R.id.filter_Rebook),
//            findViewById(R.id.filter_Lacoste),
//        )
//
//        setupFilterClickListeners()
//    }
//
//    private fun setupFilterClickListeners() {
//        setSelectedFilter(filterTextViews[0])
//
//        filterTextViews.forEachIndexed { index, textView ->
//            textView.setOnClickListener {
//                filterTextViews.forEach { it.setTextColor(Color.BLACK) }
//
//                textView.setTextColor(Color.BLUE)
//
//                when (index) {
//                    0 -> wishlistAdapter.filterProducts("All")
//                    1 -> wishlistAdapter.filterProducts("Nike")
//                    2 -> wishlistAdapter.filterProducts("Adidas")
//                    3 -> wishlistAdapter.filterProducts("Puma")
//                    4 -> wishlistAdapter.filterProducts("NewBalance")
//                    5 -> wishlistAdapter.filterProducts("Rebook")
//                    6 -> wishlistAdapter.filterProducts("Lacoste")
//                }
//            }
//        }
//    }
//
//    private fun setSelectedFilter(textView: TextView) {
//        filterTextViews.forEach { it.setTextColor(Color.BLACK) }
//
//        textView.setTextColor(Color.BLUE)
//    }
//}

