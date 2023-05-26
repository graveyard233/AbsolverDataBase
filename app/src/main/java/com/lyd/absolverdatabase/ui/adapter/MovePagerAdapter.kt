package com.lyd.absolverdatabase.ui.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.test.TodoFragment
import com.lyd.absolverdatabase.ui.page.MoveRecycleFragment
import com.lyd.absolverdatabase.utils.SideUtil

class MovePagerAdapter(val fragment :Fragment) :FragmentStateAdapter(fragment){

    companion object{
        const val PAGER_COUNT = 4
    }

    override fun getItemCount(): Int = PAGER_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){

            SideUtil.UPPER_RIGHT ->{
                MoveRecycleFragment().apply { arguments = Bundle().apply {
                    putInt("whatSide",SideUtil.UPPER_RIGHT)// 尽量把所有能传递的，初始化要用的数据都放在bundle内
                } }// 不要在这里对fragment暴露的方法进行修改，因为在这里的生命周期和想象中的不太一样，切到其他fragment然后切回来，会发现后面apply设置方法的函数并不会执行。还是用公共的viewModel来传递数据
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