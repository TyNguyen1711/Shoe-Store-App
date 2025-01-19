package com.example.shoestoreapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shoestoreapp.fragment.VoucherListFragment

class VoucherPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> VoucherListFragment.newInstance(true)
            1 -> VoucherListFragment.newInstance(false)
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}