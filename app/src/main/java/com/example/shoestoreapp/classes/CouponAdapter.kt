//package com.example.shoestoreapp.classes
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import com.example.shoestoreapp.R
//
//class CouponAdapter(
//    context: Context,
//    private val coupons: List<CouponModel>
//) : ArrayAdapter<CouponModel>(context, 0, coupons) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view = convertView ?: LayoutInflater.from(context)
//            .inflate(R.layout.card_coupon_item, parent, false)
//
//        val coupon = coupons[position]
//
//        view.findViewById<TextView>(R.id.tv_coupon_code).text = coupon.couponCode
//        view.findViewById<TextView>(R.id.tv_unlock_message).text = coupon.unlockMessage
//        view.findViewById<TextView>(R.id.tv_offer).text = coupon.offer
//
//        view.findViewById<Button>(R.id.btn_copy_code).setOnClickListener {
//            Toast.makeText(context, "Copied: ${coupon.couponCode}", Toast.LENGTH_SHORT).show()
//        }
//
//        return view
//    }
//}

package com.example.shoestoreapp.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.shoestoreapp.R
import android.content.ClipData
import android.content.ClipboardManager


class CouponAdapter(
    context: Context,
    private val coupons: List<CouponModel>
) : ArrayAdapter<CouponModel>(context, 0, coupons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.card_coupon_item, parent, false)

        val coupon = coupons[position]

        view.findViewById<TextView>(R.id.tv_coupon_code).text = coupon.couponCode
        view.findViewById<TextView>(R.id.tv_unlock_message).text = coupon.unlockMessage
        view.findViewById<TextView>(R.id.tv_offer).text = coupon.offer

        view.findViewById<Button>(R.id.btn_copy_code).setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", coupon.couponCode)

            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Copied: ${coupon.couponCode}", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

