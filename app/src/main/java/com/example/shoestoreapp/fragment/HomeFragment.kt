package com.example.shoestoreapp.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.ProductDetailActivity
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.BestSellingRepository
import com.example.shoestoreapp.data.repository.ExclusiveOfferRepository
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ProductItemAdapter.OnProductClickListener {
    private val exclusiveOfferRepository = ExclusiveOfferRepository()
    private val bestSellingRepository = BestSellingRepository()

    private lateinit var exclusiveAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter
    private var exclusiveProducts = mutableListOf<Product>()
    private val bestSellingProducts = mutableListOf<Product>()

    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var sliderAdapter: SliderAdapter

    private val sliderImages = listOf(
        R.drawable.slider_image1,
        R.drawable.slider_image2,
        R.drawable.slider_image3
    )

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

        // Setup RecyclerView for Exclusive Offers
        val exclusiveRecyclerView: RecyclerView = view.findViewById(R.id.exclusiveOfferRV)
        exclusiveAdapter = ProductItemAdapter(exclusiveProducts, this, viewLifecycleOwner)
        exclusiveRecyclerView.adapter = exclusiveAdapter
        exclusiveRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Setup RecyclerView for Best Selling
        val bestSellingRecyclerView: RecyclerView = view.findViewById(R.id.bestSellingRV)
        bestSellingAdapter = ProductItemAdapter(bestSellingProducts, this, viewLifecycleOwner)
        bestSellingRecyclerView.adapter = bestSellingAdapter
        bestSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Load data
        // loadExclusiveOfferProducts()
        // loadBestSellingProducts()

        lifecycleScope.launch {
            val result = exclusiveOfferRepository.getAllProducts()
            // Sử dụng CoroutineScope để xử lý các hàm suspend
            result.onSuccess { items ->
                exclusiveProducts.clear()
                exclusiveProducts.addAll(items)
                exclusiveAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                println("Failed to fetch exclusive offer items: ${error.message}")
            }
        }

        lifecycleScope.launch {
            val result = bestSellingRepository.getAllProducts()
            // Sử dụng CoroutineScope để xử lý các hàm suspend

            result.onSuccess { items ->
                bestSellingProducts.clear()
                bestSellingProducts.addAll(items)
                bestSellingAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                println("Failed to fetch best selling items: ${error.message}")
            }
        }

        return view
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("productId", productId)
        startActivity(intent)
    }

    override fun onMoreButtonClick() {
        Toast.makeText(requireContext(), "More Button Clicked!", Toast.LENGTH_SHORT).show()
        // Thêm logic khi nhấn "More Button" (ví dụ: chuyển tới danh sách đầy đủ)
    }
}

