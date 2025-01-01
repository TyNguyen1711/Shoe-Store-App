package com.example.shoestoreapp.fragment

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
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.repository.BestSellingRepository
import com.example.shoestoreapp.data.repository.CartRepository
import com.example.shoestoreapp.data.repository.ExclusiveOfferRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val firestore = FirebaseFirestore.getInstance()
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

//        CoroutineScope(Dispatchers.IO).launch {
//            val sampleCartItems = listOf(
//                CartItem(
//                    productId = "39QYpPN3fr9Pyw1xbezv",
//                    size = "42",
//                    quantity = 2,
//                ),
//                CartItem(
//                    productId = "4MsGcOqztbvAa8MW88oe",
//                    size = "40",
//                    quantity = 1,
//                ),
//                CartItem(
//                    productId = "6BUWKsQLaj4mQC1F2IZa",
//                    size = "38.5",
//                    quantity = 3,
//                ),
//                CartItem(
//                    productId = "FFHg5HJTo5FTwtaBHteB",
//                    size = "39",
//                    quantity = 5,
//                )
//            )
//            sampleCartItems.forEach { product ->
//                val result = CartRepository(firestore).addProductToCart("example_user_id", product)
//                result.fold(
//                    onSuccess = {
//                        println("Sản phẩm ${product.productId} đã thêm thành công!")
//                    },
//                    onFailure = { exception ->
//                        println("Lỗi khi thêm sản phẩm ${product.productId}: ${exception.message}")
//                    }
//                )
//            }
//            val result = ProductRepository(firestore).getAllProducts()
//            result.onSuccess { items ->
//                println(items)
//            }.onFailure { error ->
//                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
//                println("Failed to fetch cart items: ${error.message}")
//            }
//
//
//            // Chuyển về Main thread để cập nhật UI nếu cần
//            withContext(Dispatchers.Main) {
//
//            }
//        }

        return view
    }

    override fun onProductClick(productId: String) {

        Toast.makeText(requireContext(), "Clicked Product: $productId", Toast.LENGTH_SHORT).show()
    }

    override fun onMoreButtonClick() {
        Toast.makeText(requireContext(), "More Button Clicked!", Toast.LENGTH_SHORT).show()
        // Thêm logic khi nhấn "More Button" (ví dụ: chuyển tới danh sách đầy đủ)
    }
}