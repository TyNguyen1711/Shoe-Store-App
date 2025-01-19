package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.*
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTv = view.findViewById<TextView>(R.id.nameTv)
        val emailTv = view.findViewById<TextView>(R.id.emailTv)

        val ordersSection = view.findViewById<LinearLayout>(R.id.option_orders)
        val myDetailsSection = view.findViewById<LinearLayout>(R.id.option_myDetail)
        val deliveryAddressSection = view.findViewById<LinearLayout>(R.id.option_delivery)
        val paymentMethodsSection = view.findViewById<LinearLayout>(R.id.option_payment)
        val couponSection = view.findViewById<LinearLayout>(R.id.option_coupon)
        val notificationsSection = view.findViewById<LinearLayout>(R.id.option_notification)
        val helpSection = view.findViewById<LinearLayout>(R.id.option_help)
        val aboutSection = view.findViewById<LinearLayout>(R.id.option_about)
        val logoutButton = view.findViewById<Button>(R.id.my_button)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        val userRepos = UserRepository()
        lifecycleScope.launch {
            val user = userRepos.getUser(userId)
            user.onSuccess { userData ->
                nameTv.text = userData.fullname
                emailTv.text = userData.email
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }
        }

        // Khi bấm vào nút "Logout"
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

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
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }

        deliveryAddressSection.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        paymentMethodsSection.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentActivity::class.java))
        }
    }
}


