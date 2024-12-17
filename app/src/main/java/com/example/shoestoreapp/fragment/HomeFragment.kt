package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.ProductDetailActivity
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.google.firebase.database.DatabaseReference
import com.example.shoestoreapp.classes.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment: Fragment(), ProductItemAdapter.OnProductClickListener {
    private lateinit var database: DatabaseReference

    private lateinit var exclusiveAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter
    private val exclusiveProducts = mutableListOf<Product>()
    private val bestSellingProducts = mutableListOf<Product>()

    override fun onProductClick(productId: String) {
        // Handle click event (e.g., navigate to ProductDetailActivity)
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
                Log.e("FirebaseError", "Error: ${error.message}")
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
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }
}