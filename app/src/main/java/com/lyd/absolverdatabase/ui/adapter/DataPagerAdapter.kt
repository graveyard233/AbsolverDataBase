package com.lyd.absolverdatabase.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyd.absolverdatabase.test.ColorBoardFragment
import com.lyd.absolverdatabase.test.TodoFragment
import com.lyd.absolverdatabase.ui.page.MapFragment
import com.lyd.absolverdatabase.ui.page.TipFragment

class DataPagerAdapter(val fragment :Fragment) :FragmentStateAdapter(fragment) {

    companion object{
        const val PAGE_COUNT = 4
        const val PAGE_MAP = 0
        const val PAGE_COLOR = 1
        const val PAGE_TIP = 2
        const val PAGE_OTHER = 3
    }

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){
            PAGE_MAP ->{
                MapFragment()
            }
            PAGE_COLOR ->{
                ColorBoardFragment()
            }
            PAGE_TIP ->{
                TipFragment()
            }
            PAGE_OTHER ->{
                TodoFragment()
            }
            else -> TodoFragment()
        }
    }
}