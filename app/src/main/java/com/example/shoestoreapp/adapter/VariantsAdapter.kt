package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.ProductVariant

// Adapter for variants RecyclerView
class VariantsAdapter(
    private val variants: List<ProductVariant>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<VariantsAdapter.VariantViewHolder>() {

    class VariantViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val tvSize: TextView = view.findViewById(R.id.tv_size)
        val tvStock: TextView = view.findViewById(R.id.tv_stock)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_variant, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        val variant = variants[position]
        holder.tvSize.text = variant.size
        holder.tvStock.text = "Stock: ${variant.stock}"
        holder.btnDelete.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount() = variants.size
}