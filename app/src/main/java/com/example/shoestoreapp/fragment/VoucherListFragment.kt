package com.example.shoestoreapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.VoucherAdapter
import com.example.shoestoreapp.data.model.Coupon
//import com.example.shoestoreapp.fragment.VoucherManagementFragment
class VoucherListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VoucherAdapter
    private var isActive: Boolean = true

    companion object {
        private const val ARG_IS_ACTIVE = "is_active"

        fun newInstance(isActive: Boolean) = VoucherListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_IS_ACTIVE, isActive)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isActive = arguments?.getBoolean(ARG_IS_ACTIVE) ?: true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_voucher_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = VoucherAdapter(
            onEditClick = { coupon ->
//                (parentFragment as? VoucherManagementFragment)?.showVoucherDialog(coupon)
            },
            onDeleteClick = { coupon ->
//                (parentFragment as? VoucherManagementFragment)?.deleteVoucher(coupon)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun refreshList(vouchers: List<Coupon>) {
        val filteredVouchers = if (isActive) {
            vouchers.filter { it.quantity > 0 }
        } else {
            vouchers.filter { it.quantity == 0 }
        }
        adapter.submitList(filteredVouchers)
    }
}