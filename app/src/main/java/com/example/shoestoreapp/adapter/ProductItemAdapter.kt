    package com.example.shoestoreapp.adapter

    import android.app.Dialog
    import android.graphics.Paint
    import android.os.Handler
    import android.os.Looper
    import android.text.TextUtils
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.View.INVISIBLE
    import android.view.View.VISIBLE
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.lifecycle.LifecycleOwner
    import androidx.lifecycle.lifecycleScope
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.shoestoreapp.R
    import com.example.shoestoreapp.classes.CartDialog
    import com.example.shoestoreapp.data.model.Comment
    import com.example.shoestoreapp.data.model.Product
    import com.example.shoestoreapp.data.model.Wishlist
    import com.example.shoestoreapp.data.repository.CartRepository
    import com.example.shoestoreapp.data.repository.ProductRepository
    import com.example.shoestoreapp.data.repository.ReviewRepository
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
        private val lifecycleOwner: LifecycleOwner,
        private var isHome: Boolean = true,
        private val listName: String = ""
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var wishlists: Wishlist = Wishlist()

        companion object {
            private const val VIEW_TYPE_PRODUCT = 0
            private const val VIEW_TYPE_MORE_BUTTON = 1
        }

        interface OnProductClickListener {
            fun onProductClick(productId: String)
            fun onMoreButtonClick(listName: String)
        }

        override fun getItemViewType(position: Int): Int {
            return if ((position == productList.size) and isHome) VIEW_TYPE_MORE_BUTTON else VIEW_TYPE_PRODUCT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_PRODUCT) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.wishlist_product, parent, false)
                ProductViewHolder(view, lifecycleOwner)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.more_button, parent, false)
                MoreButtonViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ProductViewHolder) {
                val product = productList[position]
                holder.bind(product, listener, wishlists) // Truyền wishlists vào
            } else if (holder is MoreButtonViewHolder) {
                holder.bind(listName, listener)
            }
        }

        override fun getItemCount(): Int {
            return if(isHome) productList.size + 1 else productList.size  // Thêm 1 cho thẻ "More Button"
        }

        fun updateWishlist(newWishlist: Wishlist) {
            wishlists = newWishlist
            notifyDataSetChanged() // Cập nhật lại RecyclerView
        }

        class ProductViewHolder(
            itemView: View,
            private val lifecycleOwner: LifecycleOwner
        )
            : RecyclerView.ViewHolder(itemView) {
            private val auth: FirebaseAuth = FirebaseAuth.getInstance()

            private val productImage: ImageView = itemView.findViewById(R.id.productImage)
            private val productName: TextView = itemView.findViewById(R.id.fullNameTV)
            private val productPrice: TextView = itemView.findViewById(R.id.costTV)
            private val productRating: TextView = itemView.findViewById(R.id.ratingsTV)
            private val productFavoriteBtn: ImageButton = itemView.findViewById(R.id.heartBtn)
            private val productSoldCount: TextView = itemView.findViewById(R.id.soldTV)
            private val productSalePercentage: TextView = itemView.findViewById(R.id.salePercentage)
            private val cartBtn: ImageButton = itemView.findViewById(R.id.cartBtn)

            fun bind(product: Product, listener: OnProductClickListener, wishlists: Wishlist?) {
                // Gắn dữ liệu sản phẩm
                Glide.with(productImage.context).load(product.thumbnail).into(productImage)

                // Định dạng giá
                var formattedPrice: String
                if (product.discountPrice != null)
                    formattedPrice = formatCurrency(product.discountPrice)
                else
                    formattedPrice = formatCurrency(product.price)
                productPrice.text = formattedPrice

                productName.text = product.name
                val formattedRating = String.format("%.1f", product.averageRating)
                productRating.text = "$formattedRating stars"
                productSoldCount.text = product.soldCount.toString()
                productSalePercentage.text = "-${product.salePercentage}%"

                if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                    // Cập nhật trạng thái nút yêu thích
                    if (wishlists?.products?.contains(product.id) == true) {
                        productFavoriteBtn.setImageResource(R.drawable.favorite_full)
                    } else {
                        productFavoriteBtn.setImageResource(R.drawable.favorite_border)
                    }
                }

                if (wishlists!!.products?.contains(product.id) == true) {
                    productFavoriteBtn.setImageResource(R.drawable.favorite_full)
                }
                else {
                    productFavoriteBtn.setImageResource(R.drawable.favorite_border)
                }

                cartBtn.setOnClickListener() {
                    if (FirebaseAuth.getInstance().currentUser?.uid == null) {
                        showSignUpDialog(wishlists)
                    }
                    else {
                        val dialog = CartDialog(itemView.context, product, CartRepository())
                        dialog.show()
                    }
                }

                productFavoriteBtn.setOnClickListener {
                    if (FirebaseAuth.getInstance().currentUser?.uid == null) {
                        showSignUpDialog(wishlists)
                    }
                    else {
                        if (wishlists.products?.contains(product.id) == false) {
                            wishlists.products?.add(product.id)
                            productFavoriteBtn.setImageResource(R.drawable.favorite_full)
                            updateUserWishlist(wishlists)
                        } else {
                            wishlists.products?.remove(product.id)
                            productFavoriteBtn.setImageResource(R.drawable.favorite_border)
                            updateUserWishlist(wishlists)
                        }
                    }
                }

                itemView.setOnClickListener {
                    listener.onProductClick(product.id)
                }
            }

            private fun formatCurrency(amount: Double): String {
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                return formatter.format(amount)
            }

            private fun updateUserWishlist(wishlist: Wishlist) {
                lifecycleOwner.lifecycleScope.launch {
                    val wishListRepository = WishListRepository()
                    wishListRepository.updateUserWishlist(wishlist)
                    }
                }

            private fun averageRating(commentList: List<Comment>): Double {
                var sum = 0.0
                for (comment in commentList) {
                    sum += comment.rating
                }
                return sum / commentList.size
            }

            private fun showSignUpDialog(wishlists: Wishlist?) {
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
                val signupBtn = dialog.findViewById<Button>(R.id.sendBtn)
                val loginTV = dialog.findViewById<TextView>(R.id.loginTV)

                signupBtn.setOnClickListener() {
                    val username = usernameET.text.toString()
                    val email = emailET.text.toString()
                    val password = passwordET.text.toString()
                    notificationTV.text = ""

                    if (TextUtils.isEmpty(username)) {
                        notificationTV.text = itemView.context.getString(R.string.enter_username)
                        return@setOnClickListener
                    }

                    if (TextUtils.isEmpty(email)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_email)
                        return@setOnClickListener
                    }

                    if (TextUtils.isEmpty(password)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_password)
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

                                wishlists!!.userId = user.uid

                                lifecycleOwner.lifecycleScope.launch {
                                    val wishListRepository = WishListRepository()
                                    wishListRepository.addUserWishlist(wishlists)
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
                    showLoginDialog(wishlists)
                    dialog.dismiss()
                }

                dialog.show()
            }

            private fun showLoginDialog(wishlists: Wishlist?) {
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
                val loginBtn = dialog.findViewById<Button>(R.id.sendBtn)
                val signupTV = dialog.findViewById<TextView>(R.id.signupTV)

                forgotPasswordTV.setOnClickListener() {
                    forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                    showForgotPasswordDialog(wishlists)
                    dialog.dismiss()
                }

                loginBtn.setOnClickListener() {
                    val email = emailET.text.toString()
                    val password = passwordET.text.toString()
                    notificationTV.text = ""

                    if (TextUtils.isEmpty(email)) {
                        notificationTV.text = itemView.context.getString(R.string.enter_email)
                        return@setOnClickListener
                    }

                    if (TextUtils.isEmpty(password)) {
                        notificationTV.text = itemView.context.getResources().getString(R.string.enter_password)
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
                    showSignUpDialog(wishlists)
                    dialog.dismiss()
                }

                dialog.show()
            }

            private fun showForgotPasswordDialog(wishlists: Wishlist?) {
                // Hiển thị dialog quên mật khẩu
                val dialog = Dialog(itemView.context, R.style.LoginDialogStyle)
                dialog.setContentView(R.layout.dialog_forgot_password_popup)
                dialog.window?.setBackgroundDrawableResource(R.drawable.bg_window)

                val sendPB = dialog.findViewById<ProgressBar>(R.id.sendPB)
                val dismissBtn = dialog.findViewById<Button>(R.id.dismissBtn)
                val emailET = dialog.findViewById<EditText>(R.id.emailET)
                val sendBtn = dialog.findViewById<Button>(R.id.sendBtn)
                val emailTV = dialog.findViewById<TextView>(R.id.notificationTV)

                sendBtn.setOnClickListener{
                    val email = emailET.text.toString()
                    if (email.isNotEmpty()) {
                        sendPB.visibility = VISIBLE
                        sendBtn.visibility = INVISIBLE

                        auth.sendPasswordResetEmail(email).addOnSuccessListener {
                            showLoginDialog(wishlists)
                            dialog.dismiss()
                        }.addOnFailureListener { e ->
                            emailTV.text = "Error: ${e.message}"
                            sendPB.visibility = INVISIBLE
                            sendBtn.visibility = VISIBLE
                        }
                    }
                    else {
                        emailTV.text = "Email can't be error"
                    }
                }

                dismissBtn.setOnClickListener{
                    dialog.dismiss()
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

            fun bind(listName: String, listener: OnProductClickListener) {
                moreButton.setOnClickListener {
                    listener.onMoreButtonClick(listName)
                }
            }
        }
    }

