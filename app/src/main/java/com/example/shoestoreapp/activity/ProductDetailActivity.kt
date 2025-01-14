package com.example.shoestoreapp.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import at.blogc.android.views.ExpandableTextView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ImageSliderAdapter
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.Wishlist
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.WishListRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private val productRepository = ProductRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val wishListRepository = WishListRepository()
    private var wishlists: Wishlist? = Wishlist()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val documentId = intent.getStringExtra("productId")
        lifecycleScope.launch {
            val result = productRepository.getProduct(documentId!!)
            result.onSuccess { product ->
                updateUI(product)
            }.onFailure { error ->
                Log.e(TAG, "Error fetching product: ${error.message}")
                error.printStackTrace()            }
        }

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn!!.setOnClickListener() {
            finish()
        }
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return formatter.format(amount)
    }

    private fun updateUI(product: Product) {
        val productTitleTV = findViewById<TextView>(R.id.productTitleTV)
        val priceTV = findViewById<TextView>(R.id.priceTV)
        val productDescriptionTV = findViewById<ExpandableTextView>(R.id.productDescriptionTV)
        val extentBtn = findViewById<Button>(R.id.extendBtn)
        val ratingStar = findViewById<RatingBar>(R.id.ratingStar)
        val viewPageSlider = findViewById<ViewPager2>(R.id.viewPageSlider)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        val favouriteBtn = findViewById<Button>(R.id.favouriteBtn)

        // Cập nhật thông tin cơ bản
        productTitleTV.text = product.name
        val formattedPrice = formatCurrency(product.price.toDouble())
        priceTV.text = formattedPrice
        productDescriptionTV.text = product.description
        ratingStar.rating = product.averageRating.toFloat() ?: 0f

        if (userId != null) {
            lifecycleScope.launch {
                val result = wishListRepository.getUserWishlist(userId)
                Log.d("Favourite Button Repo1", "$userId")
                result.onSuccess { items ->
                    Log.d("Favourite Button Repo2", "$items")
                    wishlists = items

                    // Di chuyển logic cập nhật giao diện vào đây
                    wishlists!!.userId = userId
                    Log.d("Favourite Button UpdateUI", "${wishlists!!.products?.contains(product.id)}")
                    if (wishlists!!.products?.contains(product.id) == true) {
                        favouriteBtn.setBackgroundResource(R.drawable.favorite_full)
                    } else {
                        favouriteBtn.setBackgroundResource(R.drawable.favorite_border)
                    }
                }.onFailure { error ->
                    Log.d("Favourite Button Error", "$error")
                }
            }
        }

        // Cài đặt Adapter và xử lý sự kiện click
        val adapter = ImageSliderAdapter(product.images ?: emptyList(), 1)
        viewPageSlider.adapter = adapter

        // Kết nối DotsIndicator với ViewPager2
        dotsIndicator.attachTo(viewPageSlider)

        // Cài đặt bộ nội suy
        productDescriptionTV.setInterpolator(OvershootInterpolator())

        extentBtn.setOnClickListener() {
            if (productDescriptionTV.isExpanded) {
                productDescriptionTV.collapse()
                extentBtn.setBackgroundResource(R.drawable.extend)
            }
            else {
                productDescriptionTV.expand()
                extentBtn.setBackgroundResource(R.drawable.retract)
            }
        }

        favouriteBtn.setOnClickListener {
            if (userId == null) {
                Log.d("Favourite Button UID", "$userId")
                showSignUpDialog()
            }
            else {
                Log.d("Favourite Button UID", "$userId")
                lifecycleScope.launch {
                    val result = wishListRepository.getUserWishlist(userId)
                    result.onSuccess { items ->
                        Log.d("Favourite Button Repo", "$items")
                        wishlists = items
                    }.onFailure { error ->
                        // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                        /*                                wishlists?.userId = userId
                                                        wishListRepository.addUserWishlist(wishlists!!)*/
                    }
                }

                wishlists!!.userId = userId
                if (wishlists!!.products?.contains(product.id) == false) {
                    favouriteBtn.setBackgroundResource(R.drawable.favorite_full)
                    wishlists!!.products!!.add(product.id)
                    Log.d("Favourite Button WishList", "$wishlists")
                    updateUserWishlist(wishlists!!)
                }
                else {
                    favouriteBtn.setBackgroundResource(R.drawable.favorite_border)
                    wishlists!!.products!!.remove(product.id)
                    Log.d("Favourite Button WishList", "$wishlists")
                    updateUserWishlist(wishlists!!)
                }
            }
        }
    }

    private fun updateUserWishlist(wishlist: Wishlist) {
        lifecycleScope.launch {
            Log.d("Favourite Button UpdateWishlist", "$wishlist")
            wishListRepository.updateUserWishlist(wishlist)
        }
    }

    private fun showSignUpDialog() {
        // Hiển thị dialog đăng ký
        val dialog = Dialog(this, R.style.LoginDialogStyle)
        dialog.setContentView(R.layout.dialog_signup_popup)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_window)

        val dismissBtn = dialog.findViewById<Button>(R.id.dismissBtn)
        dismissBtn.setOnClickListener() {
            dialog.dismiss()
        }

        val notificationTV = dialog.findViewById<TextView>(R.id.signupTV_4)
        val usernameET = dialog.findViewById<EditText>(R.id.usernameET)
        val emailET = dialog.findViewById<EditText>(R.id.emailET)
        val passwordET = dialog.findViewById<TextInputEditText>(R.id.passwordET)
        val signupBtn = dialog.findViewById<Button>(R.id.signupBtn)
        val loginTV = dialog.findViewById<TextView>(R.id.loginTV)

        auth = Firebase.auth

        signupBtn.setOnClickListener() {
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            notificationTV.text = ""

            if (TextUtils.isEmpty(username)) {
                notificationTV.text = getString(R.string.enter_username)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                notificationTV.text = getResources().getString(R.string.enter_email)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                notificationTV.text = getResources().getString(R.string.enter_password)
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user!!.updateProfile(profileUpdates)

                        if (wishlists == null) {
                            wishlists = Wishlist()
                        }
                        wishlists!!.userId = user.uid

                        lifecycleScope.launch {
                            wishListRepository.addUserWishlist(wishlists!!)
                        }

                        // Return to login activity after finish sign up
                        dialog.dismiss()
                        showSuccessDialog("Sign Up Successfully")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        notificationTV.text = task.exception?.message.toString()
                        /*Toast.makeText(
                            itemView.context,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()*/
                    }
                }
        }

        loginTV.setOnClickListener {
            loginTV.setPaintFlags(loginTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            dialog.dismiss()
            showLoginDialog()
        }

        dialog.show()
    }

    private fun showLoginDialog() {
        // Hiển thị dialog đăng nhập
        val dialog = Dialog(this, R.style.LoginDialogStyle)
        dialog.setContentView(R.layout.dialog_login_popup)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_window)

        val dismissBtn = dialog.findViewById<Button>(R.id.dismissBtn)
        dismissBtn.setOnClickListener() {
            dialog.dismiss()
        }

        val notificationTV = dialog.findViewById<TextView>(R.id.loginTV_4)
        val emailET = dialog.findViewById<EditText>(R.id.emailET)
        val passwordET = dialog.findViewById<TextInputEditText>(R.id.passwordET)
        val forgotPasswordTV = dialog.findViewById<TextView>(R.id.forgotPasswordTV)
        val loginBtn = dialog.findViewById<Button>(R.id.signupBtn)
        val signupTV = dialog.findViewById<TextView>(R.id.signupTV)

        auth = Firebase.auth

        forgotPasswordTV.setOnClickListener() {
            forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            // val intent = Intent(this)
            // startActivity(intent)
        }

        loginBtn.setOnClickListener() {
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            notificationTV.text = ""

            if (TextUtils.isEmpty(email)) {
                notificationTV.text = getString(R.string.enter_email)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                notificationTV.text = getResources().getString(R.string.enter_password)
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, switch to home activity
                        dialog.dismiss()
                        showSuccessDialog("Login Successfully")
                    } else {
                        // If sign in fails, display a message to the user.
                        notificationTV.text = task.exception?.message.toString()
                        /*Toast.makeText(
                            itemView.context,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()*/
                    }
                }
        }

        signupTV.setOnClickListener() {
            signupTV.setPaintFlags(signupTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            dialog.dismiss()
            showSignUpDialog()
        }

        dialog.show()
    }

    private fun showSuccessDialog(successContent: String) {
        // Hiển thị dialog đăng nhập
        val dialog = Dialog(this, R.style.LoginDialogStyle)
        dialog.setContentView(R.layout.layout_sucess)

        val successTV = dialog.findViewById<TextView>(R.id.successTV)
        successTV.setText(successContent)

        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            // Đoạn mã cần thực thi sau delay
            dialog.dismiss()
        }, 1000) // 1000ms = 1 giây
    }
}