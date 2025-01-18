package com.example.shoestoreapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R

class FilterItemAdapter(
    private val items: MutableList<String>, // Vị trí được chọn
    private val onItemClick: (Int) -> Unit // Callback khi click vào item
) : RecyclerView.Adapter<FilterItemAdapter.ItemViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    // ViewHolder giữ tham chiếu tới các view trong item layout
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item

        // Thay đổi màu chữ nếu vị trí được chọn
        if (position == selectedPosition) {
            holder.title.setTextColor(Color.RED) // Đổi thành màu đỏ
        } else {
            holder.title.setTextColor(Color.BLACK) // Màu mặc định
        }

        // Gắn sự kiện click
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition // Cập nhật vị trí được chọn

            notifyItemChanged(previousPosition) // Cập nhật item cũ
            notifyItemChanged(selectedPosition) // Cập nhật item mới

            onItemClick(position) // Gọi callback khi item được click
        }
    }

    override fun getItemCount(): Int = items.size

    fun setPosition(pos: Int = RecyclerView.NO_POSITION) {
        selectedPosition = pos
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<String>) {
        items.clear() // Xóa danh sách cũ
        items.addAll(newItems) // Thêm danh sách mới
        selectedPosition = RecyclerView.NO_POSITION // Reset vị trí được chọn
        notifyDataSetChanged() // Thông báo thay đổi dữ liệu
    }


}


