package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Coupon

class CouponAdapter(
    private val coupons: List<Coupon>,
    private val onCouponSelected: (Coupon) -> Unit,
    private val onCouponDeselected: () -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    private var selectedPosition = -1

    inner class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val code: TextView = itemView.findViewById(R.id.code)
        val description: TextView = itemView.findViewById(R.id.description)
        val time: TextView = itemView.findViewById(R.id.time)
        val discountPercent: TextView = itemView.findViewById(R.id.discountPercent)
        val btnSelect: Button = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_coupon_item, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.code.text = coupon.code
        holder.description.text = coupon.description
        holder.time.text = coupon.threshold.toString()
        holder.discountPercent.text = coupon.discount.toString() + " %"

        if (holder.bindingAdapterPosition == selectedPosition) {
            holder.btnSelect.text = "Selected"
            holder.btnSelect.setBackgroundResource(R.drawable.selected_button_bg)
        } else {
            holder.btnSelect.text = "Collect"
            holder.btnSelect.setBackgroundResource(R.drawable.collect_button_bg)
            holder.btnSelect.isEnabled = selectedPosition == -1
        }

        holder.btnSelect.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == selectedPosition) {
                selectedPosition = -1
                onCouponDeselected()
            } else {
                val oldPosition = selectedPosition
                selectedPosition = currentPosition
                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition)
                }
                onCouponSelected(coupon)
            }
//            notifyItemChanged(currentPosition)
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int = coupons.size

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(oldPosition)
        notifyDataSetChanged()
    }
}