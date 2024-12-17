package com.example.shoestoreapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
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

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var database: DatabaseReference

    private lateinit var exclusiveRecyclerView: RecyclerView
    private lateinit var bestSellingRecyclerView: RecyclerView

    private lateinit var exclusiveAdapter: ProductItemAdapter
    private lateinit var bestSellingAdapter: ProductItemAdapter

    private val exclusiveList = mutableListOf<Product>()
    private val bestSellingList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Setup Exclusive RecyclerView
        exclusiveRecyclerView = view.findViewById(R.id.exclusiveOfferRV)
        exclusiveRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        exclusiveAdapter = ProductItemAdapter(exclusiveList)
        exclusiveRecyclerView.adapter = exclusiveAdapter

        // Setup Best Selling RecyclerView
        bestSellingRecyclerView = view.findViewById(R.id.bestSellingRC)
        bestSellingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestSellingAdapter = ProductItemAdapter(bestSellingList)
        bestSellingRecyclerView.adapter = bestSellingAdapter

        // Load data from Firebase
        loadExclusiveProducts()
        loadBestSellingProducts()

        return view

    }

    private fun loadExclusiveProducts() {
        database = FirebaseDatabase.getInstance().getReference("products/exclusive")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exclusiveList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { exclusiveList.add(it) }
                }
                exclusiveAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }

    private fun loadBestSellingProducts() {
        database = FirebaseDatabase.getInstance().getReference("products/best_selling")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bestSellingList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { bestSellingList.add(it) }
                }
                bestSellingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}