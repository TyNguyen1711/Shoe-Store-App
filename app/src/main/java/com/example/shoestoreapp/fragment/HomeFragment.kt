package com.example.shoestoreapp.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.ProductDetailActivity
import com.example.shoestoreapp.adapter.ProductItemAdapter
import com.example.shoestoreapp.adapter.SliderAdapter
import com.example.shoestoreapp.classes.Product
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ProductItemAdapter.OnProductClickListener {
    private lateinit var exclusiveOfferAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter

    private var exclusiveOfferList = mutableListOf<Product>()
    private var bestSellingList = mutableListOf<Product>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupRecyclerViews(view)
        loadDataFromFirestore()
        return view
    }

    private fun setupRecyclerViews(view: View) {
        val exclusiveOfferRecyclerView: RecyclerView = view.findViewById(R.id.exclusiveOfferRV)
        val bestSellingRecyclerView: RecyclerView = view.findViewById(R.id.bestSellingRV)

        exclusiveOfferAdapter = ProductItemAdapter(exclusiveOfferList, this)
        bestSellingAdapter = ProductItemAdapter(bestSellingList, this)

        exclusiveOfferRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        exclusiveOfferRecyclerView.adapter = exclusiveOfferAdapter
        bestSellingRecyclerView.adapter = bestSellingAdapter
    }

    private fun loadDataFromFirestore() {
        // Lấy danh sách exclusive offer
        db.collection("products")
            .document("BS")
            .collection("best selling")
            .get()
            .addOnSuccessListener { result ->
                exclusiveOfferList.clear()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    exclusiveOfferList.add(product)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                exclusiveOfferAdapter.notifyDataSetChanged()
            }

        // Lấy danh sách best selling
        db.collection("products")
            .document("BS")
            .collection("best selling")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Result Size", "Size: ${result.size()}")
                for (document in result) {
                    Log.d("Document", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error fetching documents", exception)
            }



        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Firestore Test Size", "Documents found: ${result.size()}")
                for (document in result) {
                    Log.d("Firestore Test", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Test Error", "Error fetching documents", exception)
            }
    }

    override fun onProductClick(productId: String) {
        Toast.makeText(requireContext(), "Clicked Product: $productId", Toast.LENGTH_SHORT).show()
    }

    override fun onMoreButtonClick() {
        Toast.makeText(requireContext(), "More Button Clicked!", Toast.LENGTH_SHORT).show()
        // Thêm logic khi nhấn "More Button" (ví dụ: chuyển tới danh sách đầy đủ)
    }
}

