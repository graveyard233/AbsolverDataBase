package com.lyd.absolverdatabase.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.test.TodoFragment
import com.lyd.absolverdatabase.ui.page.MoveRecycleFragment
import com.lyd.absolverdatabase.utils.SideUtil

class MovePagerAdapter(val fragment :Fragment,val forOnSelect :(MoveForSelect) ->Unit = {}) :FragmentStateAdapter(fragment){

    companion object{
        const val PAGER_COUNT = 4
    }

    override fun getItemCount(): Int = PAGER_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){

            SideUtil.UPPER_RIGHT ->{
                MoveRecycleFragment().apply { arguments = Bundle().apply {
                    putInt("whatSide",SideUtil.UPPER_RIGHT)
                } }.apply {
                    onSelect = {
                        forOnSelect.invoke(it)
                    }
                }
            }
            SideUtil.UPPER_LEFT ->{
                MoveRecycleFragment().apply { arguments = Bundle().apply {
                    putInt("whatSide",SideUtil.UPPER_LEFT)
                } }
            }
            SideUtil.LOWER_LEFT ->{
                MoveRecycleFragment().apply { arguments = Bundle().apply {
                    putInt("whatSide",SideUtil.LOWER_LEFT)
                } }
            }
            SideUtil.LOWER_RIGHT ->{
                MoveRecycleFragment().apply { arguments = Bundle().apply {
                    putInt("whatSide",SideUtil.LOWER_RIGHT)
                } }
            }
            else ->{
                TodoFragment()
            }
        }
    }

}