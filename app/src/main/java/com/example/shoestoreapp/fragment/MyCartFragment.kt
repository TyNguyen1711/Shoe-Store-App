package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.adapter.CartAdapter
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.PayActivity
import com.example.shoestoreapp.data.model.CartItem
import com.example.shoestoreapp.data.repository.CartRepository
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.WishListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkBoxAll: CheckBox
    private lateinit var editTV: TextView
    private lateinit var checkOutTV: TextView
    private lateinit var toWishListBtn: Button
    private lateinit var deleteBtn: Button

    private lateinit var topLayout: LinearLayout
    private lateinit var bottomLayout: LinearLayout
    private lateinit var couponLayout: LinearLayout

    private val firestore = FirebaseFirestore.getInstance()
    private val localCartItems = mutableListOf<CartItem>()
    private val prices = mutableListOf<Double>()
    private val names = mutableListOf<String>()
    private val thumbnail = mutableListOf<String>()
    private val stockList = mutableListOf<Int>()

    private val cartRepository = CartRepository(firestore)
    private val wishListRepository = WishListRepository(firestore)
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
    var onEdit: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho Fragment
        return inflater.inflate(R.layout.my_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        checkOutTV = view.findViewById(R.id.textViewCheckout)
        checkBoxAll = view.findViewById(R.id.checkBoxAll)
        editTV = view.findViewById(R.id.editTV)
        toWishListBtn = view.findViewById(R.id.toWishlist)
        deleteBtn = view.findViewById(R.id.delete)

        topLayout = view.findViewById(R.id.topLayout)
        bottomLayout = view.findViewById(R.id.bottomLayout)
        couponLayout = view.findViewById(R.id.couponLayout)

        // Khởi tạo CartAdapter với các callback
        cartAdapter = CartAdapter(
            products = emptyList(),
            onIncrease = { product -> updateCartItemQuantity(product, 1) },
            onDecrease = { product -> updateCartItemQuantity(product, -1) },
            onCheckedChange = { position, isChecked -> handleCheckedChange(position, isChecked) },
            images = emptyList(),
            prices = emptyList(),
            names = emptyList(),
            stockList = emptyList()
        )
        recyclerView.adapter = cartAdapter

        // Load dữ liệu giỏ hàng từ Firebase Firestore
        loadCartData(view)
        setupListeners(view)

        checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
            toggleAllCartItems(isChecked)
            updateCheckedTotalPrice(view)
        }

        bottomLayout.visibility = View.GONE
        topLayout.visibility = View.VISIBLE
        onEdit = true

        editTV.setOnClickListener() {
            if(!onEdit){
                editTV.text = "Edit"
                bottomLayout.visibility = View.GONE
                topLayout.visibility = View.VISIBLE
                couponLayout.visibility = View.VISIBLE
                onEdit = true
            }
            else
            {
                editTV.text = "Done"
                topLayout.visibility = View.GONE
                couponLayout.visibility = View.GONE
                bottomLayout.visibility = View.VISIBLE
                onEdit = false
            }
        }

        checkOutTV.setOnClickListener {
            val selectedProductIds = localCartItems
                .filter { it.isChecked } // Lọc các sản phẩm đã được check
                .map { it.productId }    // Lấy danh sách ID sản phẩm
            val selectedSize = localCartItems
                .filter { it.isChecked } // Lọc các sản phẩm đã được check
                .map { it.size }    // Lấy danh sách ID sản phẩm
            if (selectedProductIds.isEmpty()) {
                // Nếu không có sản phẩm nào được chọn, hiển thị thông báo
                Toast.makeText(requireContext(), "Please select at least one product!", Toast.LENGTH_SHORT).show()
            } else {
                // Chuyển qua PayActivity và truyền danh sách ID sản phẩm đã chọn
                val intent = Intent(requireContext(), PayActivity::class.java).apply {
                    putStringArrayListExtra("selectedProductIds", ArrayList(selectedProductIds))
                    putStringArrayListExtra("selectedSize", ArrayList(selectedSize))
                    putExtra("userId", userId)
                }
                startActivity(intent)
            }
        }
    }

    private fun handleCheckedChange(position: Int, isChecked: Boolean) {
        localCartItems[position].isChecked = isChecked
        checkBoxAll.isChecked = localCartItems.all { it.isChecked }
        cartAdapter.updateData(localCartItems)
        updateCheckedTotalPrice(requireView())
    }

    private fun toggleAllCartItems(isChecked: Boolean) {
        if(isChecked or localCartItems.all { !it.isChecked }) {
            localCartItems.forEach { it.isChecked = isChecked }
            cartAdapter.setAllChecked(isChecked)
        }
        cartAdapter.notifyDataSetChanged()
        updateCheckedTotalPrice(requireView())
    }

    private fun loadCartData(view: View) {
        val productRepository =
            ProductRepository(firestore) // Tạo một instance của ProductRepository

        val updatedCartItems = mutableListOf<CartItem>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = cartRepository.getCartItems(userId)
            // Sử dụng CoroutineScope để xử lý các hàm suspend

            result.onSuccess { cartItems ->
                for (cartItem in cartItems) {
                    val productId = cartItem.productId
                    updatedCartItems.add(cartItem)

                    // Gọi getProduct để lấy thông tin sản phẩm
                    val productResult = productRepository.getProduct(productId)
                    productResult.onSuccess { product ->
                        prices.add(product.price)
                        thumbnail.add(product.thumbnail)
                        names.add(product.name)
                        val targetVariant = product.variants.find { it.size == cartItem.size }
                        stockList.add(targetVariant!!.stock)

                    }.onFailure { error ->
                        // Xử lý lỗi nếu không lấy được sản phẩm
                        println("Failed to fetch product: ${error.message}")
                    }
                }
            }.onFailure { error ->
                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                println("Failed to fetch cart items: ${error.message}")
            }


            // Chuyển về Main thread để cập nhật UI nếu cần
            withContext(Dispatchers.Main) {
                // Xử lý updatedCartItems ở đây (ví dụ: cập nhật giao diện)
                if (localCartItems != updatedCartItems) {
                    localCartItems.clear()
                    localCartItems.addAll(updatedCartItems)
                    cartAdapter.updateData(localCartItems, thumbnail, names, prices, stockList)
                }
                view.findViewById<TextView>(R.id.productsNum).text = "(${localCartItems.size})"
                updateCheckedTotalPrice(view)
            }
        }
    }

    private fun updateCartItemQuantity(product: CartItem, change: Int) {
        val idx = localCartItems.indexOfFirst { (it.productId == product.productId) and (it.size == product.size)}
        if (idx == -1) return // Nếu sản phẩm không tồn tại trong danh sách local

        // Cập nhật UI ngay lập tức
        val updatedCartItems = localCartItems.map { it.copy() }.toMutableList()
        updatedCartItems[idx].quantity += change
        if (updatedCartItems[idx].quantity < 1) {
            updatedCartItems.removeAt(idx)
            requireView().findViewById<TextView>(R.id.productsNum).text = updatedCartItems.size.toString()
        }
        localCartItems.clear()
        localCartItems.addAll(updatedCartItems)
        cartAdapter.updateData(localCartItems)
        updateCheckedTotalPrice(requireView())

        // Gửi yêu cầu cập nhật lên Firestore
        CoroutineScope(Dispatchers.IO).launch {
            val result = cartRepository.updateProductQuantity(userId, product.productId, product.size, product.quantity + change)
            result.onSuccess { (isSuccess, _) ->
                if (isSuccess) {
                    println("Cập nhật số lượng sản phẩm thành công")
                } else {
                    println("Không thể cập nhật số lượng sản phẩm trên Firestore")
                    // Có thể hiển thị thông báo hoặc khôi phục trạng thái nếu cần
                }
            }.onFailure { error ->
                println("Failed to fetch cart items: ${error.message}")
                // Có thể hiển thị thông báo hoặc khôi phục trạng thái nếu cần
            }
        }
    }



    private fun updateCheckedTotalPrice(view: View) {
        val totalPrice = localCartItems.filter { it.isChecked }
            .sumOf { cartItem ->
                val index = localCartItems.indexOf(cartItem)
                val price = prices.getOrElse(index) { 0.0 } // Lấy giá tại index tương ứng
                cartItem.quantity * price
            }
        val numsProduct = localCartItems.count { it.isChecked }
        view.findViewById<TextView>(R.id.textViewTotal).text =
            "Total: ${String.format("%,.0f", totalPrice)}đ"

        checkOutTV.text = "Check Out\n(${numsProduct})"
    }

    private fun setupListeners (view: View) {
        deleteBtn.setOnClickListener(){
            // Cập nhật UI ngay lập tức
            val updatedCartItems = localCartItems.filter { !it.isChecked }.toMutableList()
            val idList = localCartItems.filter { it.isChecked }.map { it.productId}
            val sizeList = localCartItems.filter { it.isChecked }.map { it.size}

            localCartItems.clear()
            localCartItems.addAll(updatedCartItems)
            cartAdapter.updateData(localCartItems)
            // Gửi yêu cầu cập nhật lên Firestore
            CoroutineScope(Dispatchers.IO).launch {
                val result = cartRepository.removeProductFromCart(userId, idList, sizeList)
                result.onSuccess { (isSuccess, list) ->
                    if (isSuccess) {
                        view.findViewById<TextView>(R.id.textViewTotal).text =
                            "Total: 0.000đ"

                        checkOutTV.text = "Check Out\n(${0})"
                    } else {
                        println("Không thể cập nhật số lượng sản phẩm trên Firestore")
                        // Có thể hiển thị thông báo hoặc khôi phục trạng thái nếu cần
                    }
                }.onFailure { error ->
                    println("Failed to fetch cart items: ${error.message}")
                    // Có thể hiển thị thông báo hoặc khôi phục trạng thái nếu cần
                }
            }
        }

        toWishListBtn.setOnClickListener()
        {
            println("Ming3993")
            for(item in localCartItems) {
                println(item)
                if(item.isChecked) {
                    lifecycleScope.launch {
                        try {
                            wishListRepository.addToWishlist(
                                userId = userId,
                                productId = item.productId
                            )
                        } catch (e: Exception) {
                            Log.e("WishlistFragment", "Error removing from wishlist", e)
                        }
                    }
                }
            }
        }
    }
}