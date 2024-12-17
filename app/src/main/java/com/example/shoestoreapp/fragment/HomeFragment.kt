package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.ProductDetailActivity
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.classes.Product
import com.google.firebase.database.*
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HomeFragment : Fragment(), ProductItemAdapter.OnProductClickListener {
    private lateinit var database: DatabaseReference

    private lateinit var exclusiveAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter
    private val exclusiveProducts = mutableListOf<Product>()
    private val bestSellingProducts = mutableListOf<Product>()

    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var sliderAdapter: SliderAdapter
    private val sliderImages = listOf(
        R.drawable.slider_image1,
        R.drawable.slider_image2,
        R.drawable.slider_image3
    )

    override fun onProductClick(productId: String) {
        // Navigate to ProductDetailActivity
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId)
        startActivity(intent)
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

        // Setup RecyclerView for Exclusive Offers
        val exclusiveRecyclerView: RecyclerView = view.findViewById(R.id.exclusiveOfferRV)
        exclusiveAdapter = ProductItemAdapter(exclusiveProducts, this)
        exclusiveRecyclerView.adapter = exclusiveAdapter
        exclusiveRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Setup RecyclerView for Best Selling
        val bestSellingRecyclerView: RecyclerView = view.findViewById(R.id.bestSellingRC)
        bestSellingAdapter = ProductItemAdapter(bestSellingProducts, this)
        bestSellingRecyclerView.adapter = bestSellingAdapter
        bestSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Load data
        loadExclusiveProducts()
        loadBestSellingProducts()

        return view
    }

    private fun loadExclusiveProducts() {
        database = FirebaseDatabase.getInstance().getReference("exclusive offer")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exclusiveProducts.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { exclusiveProducts.add(it) }
                }
                exclusiveAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle Firebase error
            }
        })
    }

    private fun loadBestSellingProducts() {
        database = FirebaseDatabase.getInstance().getReference("best selling")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bestSellingProducts.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { bestSellingProducts.add(it) }
                }
                bestSellingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle Firebase error
            }
        })
    }
}
