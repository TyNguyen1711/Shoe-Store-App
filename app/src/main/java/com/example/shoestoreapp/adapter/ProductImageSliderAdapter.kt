package com.example.shoestoreapp.adapter

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ImageSliderAdapter(
    val images: List<String>,
    val mode: Int                   // 0: normal, 1: image dialog, 2: ad activity
) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_image, parent, false)
        return ImageViewHolder(view, images, mode)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position)
    }
    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View, private val images: List<String>, private val mode: Int) : RecyclerView.ViewHolder(itemView) {

        fun bind(imageUrl: String, currentPos: Int) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(imageView)

            when(mode){
                1 -> itemView.setOnClickListener {
                    imageDialog(images, currentPos)
                }
                // 2 -> itemView.setOnClickListener {
            }
        }

        private fun changeListOrdered(imageList: List<String>, currentPos: Int): List<String> {
            if (currentPos !in imageList.indices) return imageList // Nếu vị trí không hợp lệ, trả lại danh sách gốc

            val newImageList = imageList.toMutableList()
            val selectedImage = newImageList.removeAt(currentPos)
            newImageList.add(0, selectedImage) // Đưa ảnh được chọn lên đầu danh sách
            return newImageList
        }

        private fun imageDialog(imageList: List<String>, currentPos: Int) {
            val dialog = Dialog(itemView.context, R.style.ImageView)
            dialog.setContentView(R.layout.dialog_image_view)

            val viewImageSlider = dialog.findViewById<ViewPager2>(R.id.viewImageSlider)
            val dotsIndicator = dialog.findViewById<DotsIndicator>(R.id.dots_indicator)

            // Cài đặt ViewPager2 với Adapter
            val newImageList = changeListOrdered(imageList, currentPos)
            val adapter = ImageSliderAdapter(newImageList ?: emptyList(), 0)
            viewImageSlider.adapter = adapter

            // Kết nối DotsIndicator với ViewPager2
            dotsIndicator.attachTo(viewImageSlider)
            Log.d("currentPos1", currentPos.toString())
            val numberTV = dialog.findViewById<TextView>(R.id.numberImageTV)
            val dismissBtn = dialog.findViewById<Button>(R.id.dismissDialogBtn)

            Log.d("currentPos2", currentPos.toString())

            numberTV.setText("${currentPos.toString()}/${imageList.size.toString()}")

            // Lắng nghe thay đổi trang và cập nhật numberTV
            viewImageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    numberTV.text = "${position + 1}/${imageList.size}"
                }
            })

            dismissBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}