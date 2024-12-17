package com.example.shoestoreapp.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.ProductTemp
import com.example.shoestoreapp.classes.WishlistAdapter

class WishlistFragment : Fragment(), WishlistAdapter.ProductCountListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var productList: ArrayList<ProductTemp>
    private lateinit var filterTextViews: List<TextView>
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: Button
    private lateinit var productsNumTV: TextView
    private var isSearchVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        productsNumTV = view.findViewById(R.id.productsNum)
        searchEditText = view.findViewById(R.id.searchEditText)
        searchBtn = view.findViewById(R.id.searchBtn)

        setupProductList()
        setupRecyclerView()

        filterTextViews = listOf(
            view.findViewById(R.id.filter_all),
            view.findViewById(R.id.filter_Nike),
            view.findViewById(R.id.filter_Adidas),
            view.findViewById(R.id.filter_Puma),
            view.findViewById(R.id.filter_NewBalance),
            view.findViewById(R.id.filter_Rebook),
            view.findViewById(R.id.filter_Lacoste),
        )

        setupFilterClickListeners()
        setupSearchFunctionality()
    }

    private fun setupProductList() {
        productList = ArrayList()
        productList.addAll(
            listOf(
                ProductTemp(
                    id = 1,
                    name = "Nike Air Max 270",
                    price = 812000.0,
                    imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-01-800x800.jpg",
                    rating = 4.8f,
                    soldCount = 805,
                    salePercentage = "20%",
                    category = "Nike"
                ),
                ProductTemp(
                    id = 2,
                    name = "Adidas Ultraboost Light",
                    price = 3500000.0,
                    imageUrl = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-01-800x800.jpg",
                    rating = 4.7f,
                    soldCount = 345,
                    salePercentage = "20%",
                    category = "Adidas"
                ),
                ProductTemp(
                    id = 3,
                    name = "Nike Air Zoom Pegasus 40",
                    price = 3200000.0,
                    imageUrl = "https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2022/12/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-63a95bbc52f23-26122022153052.jpg",
                    rating = 4.6f,
                    soldCount = 512,
                    salePercentage = "15%",
                    category = "Nike"
                ),
                ProductTemp(
                    id = 4,
                    name = "Adidas EQ21 Nữ - Trắng Tím",
                    price = 950000.0,
                    imageUrl = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
                    rating = 4.5f,
                    soldCount = 320,
                    salePercentage = "25%",
                    category = "Adidas"
                ),
                ProductTemp(
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
        )
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter(requireContext(), productList, this)
        recyclerView.adapter = wishlistAdapter
        productsNumTV.text = "(${productList.size})"
    }

    override fun onProductCountChanged(count: Int) {
        productsNumTV.text = "($count)"
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
