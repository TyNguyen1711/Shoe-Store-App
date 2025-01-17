package com.example.shoestoreapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.Coupon
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class VoucherManagementFragment : Fragment() {
    private val voucherList = mutableListOf<Coupon>()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voucher_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        view.findViewById<ImageButton>(R.id.btnAdd).setOnClickListener {
            showVoucherDialog()
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int) =
                VoucherListFragment.newInstance(position == 0)
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Còn hiệu lực" else "Hết hiệu lực"
        }.attach()
    }

    internal fun showVoucherDialog(coupon: Coupon? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_voucher, null)

        val etCode = dialogView.findViewById<EditText>(R.id.etCode)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val etDiscount = dialogView.findViewById<EditText>(R.id.etDiscount)
        val etThreshold = dialogView.findViewById<EditText>(R.id.etThreshold)
        coupon?.let {
            etCode.setText(it.code)
            etDescription.setText(it.description)
            etQuantity.setText(it.quantity.toString())
            etDiscount.setText(it.discount.toString())
            etThreshold.setText(it.threshold.toString())
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (coupon == null) "Thêm Voucher" else "Sửa Voucher")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                val newCoupon = Coupon(
                    id = coupon?.id ?: System.currentTimeMillis().toString(),
                    code = etCode.text.toString(),
                    description = etDescription.text.toString(),
                    quantity = etQuantity.text.toString().toIntOrNull() ?: 0,
                    discount = etDiscount.text.toString().toIntOrNull() ?: 0,
                    threshold = etThreshold.text.toString().toIntOrNull() ?: 0,
                )

                if (coupon == null) addVoucher(newCoupon) else updateVoucher(newCoupon)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun addVoucher(coupon: Coupon) {
        voucherList.add(coupon)
        refreshLists()
    }

    private fun updateVoucher(coupon: Coupon) {
        val index = voucherList.indexOfFirst { it.id == coupon.id }
        if (index != -1) {
            voucherList[index] = coupon
            refreshLists()
        }
    }

    fun deleteVoucher(coupon: Coupon) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa Voucher")
            .setMessage("Bạn có chắc chắn muốn xóa voucher này?")
            .setPositiveButton("Xóa") { _, _ ->
                voucherList.remove(coupon)
                refreshLists()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun refreshLists() {
        childFragmentManager.fragments.forEach {
            (it as? VoucherListFragment)?.refreshList(voucherList)
        }
    }
}
