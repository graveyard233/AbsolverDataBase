package com.lyd.absolverdatabase.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyd.absolverdatabase.test.TodoFragment
import com.lyd.absolverdatabase.utils.SideUtil

class MovePagerAdapter(val fragment :Fragment) :FragmentStateAdapter(fragment){

    companion object{
        const val PAGER_COUNT = 4
    }

    override fun getItemCount(): Int = PAGER_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){
            SideUtil.UPPER_RIGHT ->{
                TodoFragment()
            }
            SideUtil.UPPER_LEFT ->{
                TodoFragment()
            }
            SideUtil.LOWER_LEFT ->{
                TodoFragment()
            }
            SideUtil.LOWER_RIGHT ->{
                TodoFragment()
            }
            else ->{
                TodoFragment()
            }
        }
    }

}