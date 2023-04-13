package com.lyd.absolverdatabase.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyd.absolverdatabase.test.Test1Fragment
import com.lyd.absolverdatabase.ui.page.MapFragment

class DataPagerAdapter(val fragment :Fragment) :FragmentStateAdapter(fragment) {

    companion object{
        const val PAGE_COUNT = 2
        const val PAGE_MAP = 0
        const val PAGE_OTHER = 1
    }

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){
            PAGE_MAP ->{
                MapFragment()
            }
            PAGE_OTHER ->{
                Test1Fragment()
            }
            else -> Test1Fragment()
        }
    }
}