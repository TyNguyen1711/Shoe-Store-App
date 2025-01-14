package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Coupon

class CouponCartAdapter(
    private val coupons: List<Coupon>,
    private val onCouponSelected: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponCartAdapter.CouponViewHolder>() {

    inner class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val code: TextView = itemView.findViewById(R.id.code)
        val description: TextView = itemView.findViewById(R.id.description)
        val time: TextView = itemView.findViewById(R.id.time)
        val discountPercent: TextView = itemView.findViewById(R.id.discountPercent)
        val btnSelect: Button = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_coupon_cart_item, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.code.text = coupon.code
        holder.description.text = coupon.description
        holder.time.text = coupon.endTime
        holder.discountPercent.text = "${coupon.discount} %"

        // Gắn sự kiện khi bấm nút
        holder.btnSelect.setOnClickListener {
            // Gọi callback khi coupon được chọn
            onCouponSelected(coupon)
        }
    }

    override fun getItemCount(): Int = coupons.size
}
