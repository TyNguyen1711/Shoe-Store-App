package com.example.shoestoreapp.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.WishlistAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.CartRepository
import com.example.shoestoreapp.data.repository.WishListRepository
import kotlinx.coroutines.launch


class WishlistFragment : Fragment(), WishlistAdapter.ProductCountListener,
    WishlistAdapter.OnHeartClickListener,
    WishlistAdapter.OnCartClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var productList: ArrayList<Product>
    private lateinit var filterTextViews: List<TextView>
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: Button
    private lateinit var productsNumTV: TextView
    private lateinit var cartBtn: Button
    private var isSearchVisible = false
    private val wishlistRepository = WishListRepository()
    private val cartRepository = CartRepository()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onHeartClick(product: Product, position: Int) {

        wishlistAdapter.removeItem(position)

        val updatedProductCount = wishlistAdapter.itemCount
        productsNumTV.text = "($updatedProductCount)"

        lifecycleScope.launch {
            try {
                wishlistRepository.removeFromWishlist(
                    userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2",
                    productId = product.id
                )
                val result = wishlistRepository.getWishlistByUserId(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2")
                if (result.isSuccess) {
                    val updatedProducts = result.getOrNull() ?: emptyList()
                    wishlistAdapter.updateData(updatedProducts)
                    productsNumTV.text = "(${updatedProducts.size})"
                }
            } catch (e: Exception) {
                wishlistAdapter.undoRemove(position, product)
                Log.e("WishlistFragment", "Error removing from wishlist", e)
            }
        }
    }

    override fun onCartClick(product: Product) {
        lifecycleScope.launch {
            try {
//                cartRepository.addProductToCart(
//                    userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2",
//                    product = product
//                )
                Log.d("t", "t")
            } catch (e: Exception) {
                Log.e("WishlistFragment", "fd", e)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        productsNumTV = view.findViewById(R.id.productsNum)
        searchEditText = view.findViewById(R.id.searchEditText)
        searchBtn = view.findViewById(R.id.searchBtn)
        cartBtn = view.findViewById(R.id.cartBtn)

        productList = ArrayList()


        setupRecyclerView()
        fetchProductList()

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

        cartBtn.setOnClickListener {
            val cartFragment = MyCartFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit()
        }
    }
    private fun fetchProductList() {
        lifecycleScope.launch {
            val result =
                wishlistRepository.getWishlistByUserId(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2")
            if (result.isSuccess) {
                val products = result.getOrNull() ?: emptyList()
                wishlistAdapter.updateData(products)
                productsNumTV.text = "(${products.size})"
            } else {
                val error = result.exceptionOrNull()
                Log.e("WishlistFragment", "Error fetching products: $error")
            }
        }
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter(
            requireContext(),
            productList,
            productCountListener = this,
            onHeartClickListener = this,
            onCartClickListener = this
        )
        recyclerView.adapter = wishlistAdapter
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
