package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Address

class AddressAdapter(
    private val addressList: List<Address>,
    private val onAddressSelected: (Address) -> Unit,
    private val onEditAddress: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var selectedAddressId: String? = null

    // Hàm đặt địa chỉ mặc định khi vào màn hình
    fun setDefaultAddress(defaultAddressId: String?) {
        selectedAddressId = defaultAddressId
        notifyDataSetChanged() // Cập nhật lại giao diện
    }

    // Hàm thay đổi địa chỉ được chọn
    fun selectAddress(addressId: String) {
        selectedAddressId = addressId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.bind(address, address.id == selectedAddressId)
        holder.itemView.setOnClickListener {
            if (selectedAddressId != address.id) {
                selectedAddressId = address.id
                onAddressSelected(address)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = addressList.size

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullNameTV: TextView = itemView.findViewById(R.id.fullNameTV)
        private val phoneNumberTV: TextView = itemView.findViewById(R.id.phoneNummberTV)
        private val houseNoTV: TextView = itemView.findViewById(R.id.houseNoTV)
        private val cityTV: TextView = itemView.findViewById(R.id.cityTV)
        private val isDefaultRB: RadioButton = itemView.findViewById(R.id.isDefaultRB)
        private val editAddressTV: TextView = itemView.findViewById(R.id.editAddressTV)
        private val isDefaultTextView: TextView = itemView.findViewById(R.id.isDefaultTV)

        fun bind(address: Address, isSelected: Boolean) {
            fullNameTV.text = address.fullName
            phoneNumberTV.text = address.phoneNumber
            houseNoTV.text = address.houseNo
            cityTV.text = address.city

            // Hiển thị trạng thái mặc định
            isDefaultTextView.text = if (address.isDefault) "Default" else ""

            // Hiển thị trạng thái được chọn
            isDefaultRB.isChecked = isSelected

            // Xử lý sự kiện chọn RadioButton
            isDefaultRB.setOnClickListener {
                if (selectedAddressId != address.id) {
                    selectedAddressId = address.id
                    onAddressSelected(address)
                    notifyDataSetChanged()
                }
            }

            // Xử lý sự kiện chỉnh sửa địa chỉ
            editAddressTV.setOnClickListener {
                onEditAddress(address)
            }
        }
    }
}