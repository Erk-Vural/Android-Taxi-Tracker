package com.project.alpha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.alpha.fragments.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //< create >
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //</ create >

        //< get elements >
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
        //</ get elements >
        val adapter = ViewPagerAdapter(this)
        viewPager2.adapter = adapter
        viewPager2.isUserInputEnabled = false
        TabLayoutMediator(
            tabLayout, viewPager2
        ) { tab, position -> tab.text = "Type " + (position + 1) }.attach()
    }
}