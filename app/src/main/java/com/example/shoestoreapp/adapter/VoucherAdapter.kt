
package com.example.shoestoreapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Coupon
import android.widget.ImageButton
class VoucherAdapter(
    private val onEditClick: (Coupon) -> Unit,
    private val onDeleteClick: (Coupon) -> Unit
) : ListAdapter<Coupon, VoucherAdapter.VoucherViewHolder>(VoucherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val tvDiscount: TextView = itemView.findViewById(R.id.tvDiscount)
        private val tvThreshold: TextView = itemView.findViewById(R.id.tvThreshold)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(coupon: Coupon) {
            tvCode.text = coupon.code
            tvDescription.text = coupon.description
            tvQuantity.text = "Số lượng: ${coupon.quantity}"
            tvDiscount.text = "Giảm giá: ${coupon.discount}%"
            tvThreshold.text = "Ngưỡng sử dụng: ${coupon.threshold}"
            btnEdit.setOnClickListener { onEditClick(coupon) }
            btnDelete.setOnClickListener { onDeleteClick(coupon) }
        }
    }

    class VoucherDiffCallback : DiffUtil.ItemCallback<Coupon>() {
        override fun areItemsTheSame(oldItem: Coupon, newItem: Coupon) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Coupon, newItem: Coupon) = oldItem == newItem
    }
}
