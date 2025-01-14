package com.example.shoestoreapp.activity

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.ImageView

class StatusSwitcher(
    private val context: Context,
    private val textViews: List<TextView>,
    private val imageViews: List<ImageView>, // Danh sách các ImageView
    private val selectedBackground: Int,
    private val defaultBackground: Int,
    defaultSelected: TextView? = null
) {
    private var currentSelected: TextView? = null

    init {
        // Gắn sự kiện click cho tất cả TextView
        textViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectTextView(textView, index)
            }
        }

        // Đặt trạng thái mặc định nếu có
        defaultSelected?.let { textView ->
            val defaultIndex = textViews.indexOf(textView)
            selectTextView(textView, defaultIndex)
        }
    }

    // Hàm để chọn một TextView
    private fun selectTextView(textView: TextView, index: Int) {
        // Reset TextView cũ
        currentSelected?.setBackgroundResource(defaultBackground)
        imageViews.forEach { it.visibility = View.INVISIBLE } // Ẩn tất cả ImageView

        // Cập nhật TextView mới
        textView.setBackgroundResource(selectedBackground)
        imageViews[index].visibility = View.VISIBLE // Hiện ImageView tương ứng
        currentSelected = textView
    }
}