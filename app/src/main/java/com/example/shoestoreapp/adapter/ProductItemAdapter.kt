    package com.example.shoestoreapp.adapter

    import android.app.Dialog
    import android.content.Intent
    import android.graphics.Paint
    import android.os.Handler
    import android.os.Looper
    import android.text.TextUtils
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.lifecycle.LifecycleOwner
    import androidx.lifecycle.lifecycleScope
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.shoestoreapp.R
    import com.example.shoestoreapp.data.model.Product
    import com.example.shoestoreapp.data.model.Wishlist
    import com.example.shoestoreapp.data.repository.WishListRepository
    import com.google.android.material.textfield.TextInputEditText
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.auth
    import com.google.firebase.auth.userProfileChangeRequest
    import kotlinx.coroutines.launch
    import java.text.NumberFormat
    import java.util.Locale

    class ProductItemAdapter(
        private val productList: List<Product>,
        private val listener: OnProductClickListener,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val VIEW_TYPE_PRODUCT = 0
            private const val VIEW_TYPE_MORE_BUTTON = 1
        }

        interface OnProductClickListener {
            fun onProductClick(productId: String)
            fun onMoreButtonClick()
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == productList.size) VIEW_TYPE_MORE_BUTTON else VIEW_TYPE_PRODUCT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_PRODUCT) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.product_details, parent, false)
                ProductViewHolder(view, lifecycleOwner)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.more_button, parent, false)
                MoreButtonViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ProductViewHolder) {
                val product = productList[position]
                holder.bind(product, listener)
            } else if (holder is MoreButtonViewHolder) {
                holder.bind(listener)
            }
        }

        override fun getItemCount(): Int {
            return productList.size + 1 // Thêm 1 cho thẻ "More Button"
        }

        class ProductViewHolder(itemView: View, private val lifecycleOwner: LifecycleOwner)
            : RecyclerView.ViewHolder(itemView) {
            private lateinit var auth: FirebaseAuth

            private val productImage: ImageView = itemView.findViewById(R.id.productImage)
            private val productName: TextView = itemView.findViewById(R.id.fullNameTV)
            private val productPrice: TextView = itemView.findViewById(R.id.costTV)
            private val productRating: TextView = itemView.findViewById(R.id.ratingsTV)
            private val productFavoriteBtn: ImageButton = itemView.findViewById(R.id.favoriteBtn)
            private val productSoldCount: TextView = itemView.findViewById(R.id.soldTV)

            private var wishlists: Wishlist? = null
            private val wishListRepository = WishListRepository()

            private val userId = FirebaseAuth.getInstance().currentUser?.uid

            fun bind(product: Product, listener: OnProductClickListener) {
                // Gắn dữ liệu sản phẩm
                Glide.with(productImage.context).load(product.thumbnail).into(productImage)

                // Định dạng giá
                val formattedPrice = formatCurrency(product.price.toDouble())
                productPrice.text = formattedPrice

                productName.text = product.name
                productRating.text = "${product.averageRating} stars"
                productSoldCount.text = product.soldCount.toString()

                productFavoriteBtn.setOnClickListener {
                    Log.d("UserID", "$userId")
                    if (userId == null)
                        showSignUpDialog()
                    else {
                        lifecycleOwner.lifecycleScope.launch {
                            val result = wishListRepository.getUserWishlist(userId)
                            Log.d("WishList Result", "$result")
                            result.onSuccess { items ->
                                wishlists = items
                            }.onFailure { error ->
                                // Xử lý lỗi nếu không lấy được danh sách giỏ hàng
                                wishlists?.userId = userId
                                wishListRepository.addUserWishlist(wishlists!!)
                            }
                        }

                        if (wishlists!!.products?.contains(product.id) == false) {
                            productFavoriteBtn.setImageResource(R.drawable.favorite_full)
                            wishlists!!.products!!.add(product.id)
                            updateUserWishlist(wishlists!!)
                        }
                        else {
                            productFavoriteBtn.setImageResource(R.drawable.favorite_border)
                            wishlists!!.products!!.remove(product.id)
                            updateUserWishlist(wishlists!!)
                        }
                    }
                }

                itemView.setOnClickListener {
                    listener.onProductClick(product.id.toString())
                }
            }

            private fun formatCurrency(amount: Double): String {
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                return formatter.format(amount)
            }

            private fun updateUserWishlist(wishlist: Wishlist) {
                lifecycleOwner.lifecycleScope.launch {
                    wishListRepository.updateUserWishlist(wishlist)
                    }
                }

            private fun showSignUpDialog() {
                // Hiển thị dialog đăng ký
                val dialog = Dialog(itemView.context, R.style.LoginDialogStyle)
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

                    if (TextUtils.isEmpty(username)) {
                        notificationTV.text = itemView.context.getString(R.string.enter_username)
                    }

                    if (TextUtils.isEmpty(email)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_email)
                    }

                    if (TextUtils.isEmpty(password)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_password)
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Set username through update user profile

                                val user = Firebase.auth.currentUser
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = username
                                }
                                user!!.updateProfile(profileUpdates)

                                // Return to login activity after finish sign up
                                dialog.dismiss()
                                showSuccessDialog("Sign Up Successfully")
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    itemView.context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
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
                val dialog = Dialog(itemView.context, R.style.LoginDialogStyle)
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

                    if (TextUtils.isEmpty(email)) {
                        notificationTV.text = itemView.context.getString(R.string.enter_email)
                    }

                    if (TextUtils.isEmpty(password)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_password)
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                // Sign in success, switch to home activity
                                dialog.dismiss()
                                showSuccessDialog("Login Successfully")
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    itemView.context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
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
                val dialog = Dialog(itemView.context, R.style.LoginDialogStyle)
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

        class MoreButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val moreButton: ImageButton = itemView.findViewById(R.id.moreBtn)

            fun bind(listener: OnProductClickListener) {
                moreButton.setOnClickListener {
                    listener.onMoreButtonClick()
                }
            }
        }
    }

