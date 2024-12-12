package com.example.shoestoreapp.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shoestoreapp.fragment.ContactFragment
import com.example.shoestoreapp.fragment.FaqFragment


class TabPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        FaqFragment(),
        ContactFragment(),
    )

    private val titles = listOf(
        "FAQ",
        "Contact us"
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    fun getTabTitle(position: Int): String {
        return titles[position]
    }
}
