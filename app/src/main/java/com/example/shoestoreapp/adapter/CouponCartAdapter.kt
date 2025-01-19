package com.example.shoestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Coupon
import java.text.DecimalFormat

class CouponCartAdapter(
    private val coupons: List<Coupon>,
    private val totalPrice: String,
    private val onCouponSelected: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponCartAdapter.CouponViewHolder>() {

    private val sortedCoupons = coupons.sortedByDescending { coupon ->
        // Đưa coupon có alpha = 1f lên đầu, alpha = 0.5f xuống dưới
        if (coupon.threshold.toDouble() <= totalPrice.toDouble()) 1 else 0
    }

    inner class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val code: TextView = itemView.findViewById(R.id.code)
        val description: TextView = itemView.findViewById(R.id.description)
        val time: TextView = itemView.findViewById(R.id.time)
        val threshold: TextView = itemView.findViewById(R.id.threshold)
        val discountPercent: TextView = itemView.findViewById(R.id.discountPercent)
        val btnSelect: Button = itemView.findViewById(R.id.btnSelect)
        val cardView: View =
            itemView.findViewById(R.id.cardCoupon)  // Giả sử có một card view chứa coupon
        val moreTV: TextView = itemView.findViewById(R.id.moreValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_coupon_cart_item, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("#,###")
        val coupon = coupons[position]
        holder.code.text = coupon.code
        holder.description.text = coupon.description
        val thresholdValue = coupon.threshold.toString().toDoubleOrNull()
        holder.time.text = coupon.quantity.toString()
        holder.threshold.text = "${String.format("%,.0f", thresholdValue)}"
        holder.discountPercent.text = "${coupon.discount} %"

        val threshold = coupon.threshold.toString().toDoubleOrNull()
        val totalPriceDouble = totalPrice.toDoubleOrNull()
        Log.d("Threshold", threshold.toString())
        Log.d("Total", totalPriceDouble.toString())

        if (threshold != null && totalPriceDouble != null && totalPriceDouble < threshold) {
            val more = threshold - totalPriceDouble
            val formattedMore = decimalFormat.format(more)
            val textToShow: String = "Buy $formattedMore more to apply."
            holder.moreTV.text = textToShow
            holder.moreTV.visibility = VISIBLE
            // Nếu threshold nhỏ hơn totalPrice, làm mờ item
            holder.cardView.alpha = 0.5f  // Thay đổi độ mờ của card
        } else {
            holder.cardView.alpha = 1f  // Bảo đảm card không bị mờ khi điều kiện không thỏa mãn
        }

        // Gắn sự kiện khi bấm nút
        holder.btnSelect.setOnClickListener {
            // Gọi callback khi coupon được chọn
            onCouponSelected(coupon)
        }

    }

    override fun getItemCount(): Int = sortedCoupons.size
}