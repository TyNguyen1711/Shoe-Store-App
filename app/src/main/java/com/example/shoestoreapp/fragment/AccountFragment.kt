package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.*
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var nameTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var userRepos: UserRepository
    private lateinit var userId: String
    private lateinit var avatar: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các view
        nameTv = view.findViewById(R.id.nameTv)
        emailTv = view.findViewById(R.id.emailTv)
        avatar = view.findViewById(R.id.avatar)

        val ordersSection = view.findViewById<LinearLayout>(R.id.option_orders)
        val myDetailsSection = view.findViewById<LinearLayout>(R.id.option_myDetail)
        val deliveryAddressSection = view.findViewById<LinearLayout>(R.id.option_delivery)
        val paymentMethodsSection = view.findViewById<LinearLayout>(R.id.option_payment)
        val couponSection = view.findViewById<LinearLayout>(R.id.option_coupon)
        val notificationsSection = view.findViewById<LinearLayout>(R.id.option_notification)
        val helpSection = view.findViewById<LinearLayout>(R.id.option_help)
        val aboutSection = view.findViewById<LinearLayout>(R.id.option_about)
        val logoutButton = view.findViewById<Button>(R.id.my_button)

        // Lấy userId từ Firebase
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        userRepos = UserRepository()

        // Lấy thông tin người dùng
        loadUserData()

        // Khi bấm vào nút "Logout"
        logoutButton.setOnClickListener {
            // Đăng xuất người dùng
            FirebaseAuth.getInstance().signOut()

            // Chuyển hướng người dùng về menu
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }

        couponSection.setOnClickListener {
            startActivity(Intent(requireContext(), CouponActivity::class.java))
        }

        notificationsSection.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        helpSection.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        aboutSection.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        ordersSection.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java))
        }

        myDetailsSection.setOnClickListener {
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }

        deliveryAddressSection.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        paymentMethodsSection.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentActivity::class.java))
        }
    }

    // Hàm này để lấy và hiển thị lại dữ liệu người dùng
    private fun loadUserData() {
        lifecycleScope.launch {
            val user = userRepos.getUser(userId)
            user.onSuccess { userData ->
                nameTv.text = if(userData.fullname.isNotEmpty()) userData.fullname else userData.username
                emailTv.text = userData.email
                if (userData.avatar.isNotEmpty())
                {
                Glide.with(requireContext())
                    .load(userData.avatar) // Glide sẽ tải ảnh từ URL
                    .transform(CircleCrop())
                    .into(avatar) // Đặt ảnh vào ImageView avatar
                }
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }
        }
    }

    // Gọi lại hàm loadUserData mỗi khi fragment trở lại foreground
    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}
