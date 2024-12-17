package com.example.shoestoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.*

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ordersSection = view.findViewById<LinearLayout>(R.id.option_orders)
        val myDetailsSection = view.findViewById<LinearLayout>(R.id.option_myDetail)
        val deliveryAddressSection = view.findViewById<LinearLayout>(R.id.option_delivery)
        val paymentMethodsSection = view.findViewById<LinearLayout>(R.id.option_payment)
        val couponSection = view.findViewById<LinearLayout>(R.id.option_coupon)
        val notificationsSection = view.findViewById<LinearLayout>(R.id.option_notification)
        val helpSection = view.findViewById<LinearLayout>(R.id.option_help)
        val aboutSection = view.findViewById<LinearLayout>(R.id.option_about)
        val logoutButton = view.findViewById<Button>(R.id.my_button)

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


    }
}
