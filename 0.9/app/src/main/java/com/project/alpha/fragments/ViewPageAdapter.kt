package com.project.alpha.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Type1()
            1 -> Type2()
            else -> Type3()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }


}