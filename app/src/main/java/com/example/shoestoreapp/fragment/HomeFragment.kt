package com.example.shoestoreapp.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.DisplayProductListActivity
import com.example.shoestoreapp.activity.ProductDetailActivity
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.BestSellingRepository
import com.example.shoestoreapp.data.repository.ExclusiveOfferRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.WishListRepository
import com.google.firebase.auth.FirebaseAuth
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch

class HomeFragment :
    Fragment(),
    ProductItemAdapter.OnProductClickListener
{
    private val exclusiveOfferRepository = ExclusiveOfferRepository()
    private val bestSellingRepository = BestSellingRepository()
    private val brandProductRepository = ProductRepository()

    private lateinit var exclusiveAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter
    private var exclusiveProducts = mutableListOf<Product>()
    private val bestSellingProducts = mutableListOf<Product>()
    private val wishListRepository = WishListRepository()

    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var sliderAdapter: SliderAdapter

    private lateinit var filterTextViews: List<TextView>
    private var brandProductList = mutableListOf<Product>()
    private lateinit var brandProductAdapter: ProductItemAdapter

    private val sliderImages = listOf(
        R.drawable.slider_image1,
        R.drawable.slider_image2,
        R.drawable.slider_image3
    )

    override fun onResume() {
        super.onResume()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            lifecycleScope.launch {
                val result = wishListRepository.getUserWishlist(userId)
                result.onSuccess { items ->
                    exclusiveAdapter.updateWishlist(items)
                    bestSellingAdapter.updateWishlist(items)
                }.onFailure { error ->
                    Log.e("HomeFragment", "Failed to fetch wishlist: ${error.message}")
                }
            }
        }
    }

    private fun setSelectedFilter(textView: TextView) {
        filterTextViews.forEach { it.setTextColor(Color.BLACK) }
        textView.setTextColor(Color.BLUE)
    }

    private fun setupFilterClickListeners() {
        setSelectedFilter(filterTextViews[0])

        filterTextViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                filterTextViews.forEach { it.setTextColor(Color.BLACK) }

                textView.setTextColor(Color.BLUE)
                lifecycleScope.launch {
                    var result = brandProductRepository.getAllProducts()
                    var brand = "All"
                    when (index) {
                        0 -> result = brandProductRepository.getAllProducts()
                        1 -> {
                            brand = "Nike"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                        2 -> {
                            brand = "Adidas"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                        3 -> {
                            brand = "Puma"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                        4 -> {
                            brand = "New Balance"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                        5 -> {
                            brand = "Rebook"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                        6 -> {
                            brand = "Lacoste"
                            result = brandProductRepository.getProductsByBrand(brand)
                        }
                    }
                    result.onSuccess { items ->
                        brandProductList.clear()
                        if (items.size > 5) {
                            brandProductList.addAll(items.subList(0, 5))
                        } else {
                            brandProductList.addAll(items)
                        }
                        brandProductAdapter = ProductItemAdapter(brandProductList, this@HomeFragment, viewLifecycleOwner,true, brand)
                    }.onFailure { error ->
                        Log.e("HomeFragment", "Failed to fetch brand products: ${error.message}")
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Setup Slider
        viewPagerSlider = view.findViewById(R.id.viewPageSlider)
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        sliderAdapter = SliderAdapter(sliderImages)
        viewPagerSlider.adapter = sliderAdapter
        dotsIndicator.setViewPager2(viewPagerSlider)
        filterTextViews = listOf(
            view.findViewById(R.id.filter_all),
            view.findViewById(R.id.filter_Nike),
            view.findViewById(R.id.filter_Adidas),
            view.findViewById(R.id.filter_Puma),
            view.findViewById(R.id.filter_NewBalance),
            view.findViewById(R.id.filter_Rebook),
            view.findViewById(R.id.filter_Lacoste),
        )

        // Setup RecyclerView for Brand
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        brandProductAdapter = ProductItemAdapter(brandProductList, this, viewLifecycleOwner,true, "All")
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = brandProductAdapter

        // Setup RecyclerView for Exclusive Offers
        val exclusiveRecyclerView: RecyclerView = view.findViewById(R.id.exclusiveOfferRV)
        exclusiveAdapter = ProductItemAdapter(exclusiveProducts, this, viewLifecycleOwner,true, "ExclusiveOffer")
        exclusiveRecyclerView.adapter = exclusiveAdapter
        exclusiveRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Setup RecyclerView for Best Selling
        val bestSellingRecyclerView: RecyclerView = view.findViewById(R.id.bestSellingRV)
        bestSellingAdapter = ProductItemAdapter(bestSellingProducts, this, viewLifecycleOwner,true, "BestSelling")
        bestSellingRecyclerView.adapter = bestSellingAdapter
        bestSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        setupFilterClickListeners()
        lifecycleScope.launch {
            val exclusiveOfferRepo = exclusiveOfferRepository.getAllProducts()
            val bestSellingRepo = bestSellingRepository.getAllProducts()
            val brandProductRepo = brandProductRepository.getAllProducts()

            // Sử dụng CoroutineScope để xử lý các hàm suspend
            exclusiveOfferRepo.onSuccess { items ->
                exclusiveProducts.clear()
                if (items.size > 5) {
                    exclusiveProducts.addAll(items.subList(0, 5))
                } else {
                    exclusiveProducts.addAll(items)
                }
                exclusiveAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                Log.e("HomeFragment", "Failed to fetch exclusive offer products: ${error.message}")
            }

            bestSellingRepo.onSuccess { items ->
                bestSellingProducts.clear()
                if (items.size > 5) {
                    bestSellingProducts.addAll(items.subList(0, 5))
                } else {
                    bestSellingProducts.addAll(items)
                }
                bestSellingAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                Log.e("HomeFragment", "Failed to fetch best selling products: ${error.message}")
            }


        brandProductRepo.onSuccess { items ->
                brandProductList.clear()
                if (items.size > 7) {
                    brandProductList.addAll(items.subList(0, 5))
                } else {
                    brandProductList.addAll(items)
                }
                brandProductAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                Log.e("HomeFragment", "Failed to fetch brand products: ${error.message}")
            }
        }

        return view
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("productId", productId)
        startActivity(intent)
    }

    override fun onMoreButtonClick(listName: String) {
        Toast.makeText(requireContext(), "More Button Clicked!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), DisplayProductListActivity::class.java)
        intent.putExtra("listName", listName)
        startActivity(intent)
        // Thêm logic khi nhấn "More Button" (ví dụ: chuyển tới danh sách đầy đủ)
    }
}

