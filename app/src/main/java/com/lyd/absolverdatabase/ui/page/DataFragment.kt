package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DataState
import com.lyd.absolverdatabase.databinding.FragmentDataBinding
import com.lyd.absolverdatabase.ui.adapter.DataPagerAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment

class DataFragment : BaseFragment() {

    private var dataBinding : FragmentDataBinding ? = null
    private var dataState : DataState ?= null

    private lateinit var viewPagerAdapter: DataPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout :TabLayout

    private val viewPagerCallback :ViewPager2.OnPageChangeCallback by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1){
//                    hideTab()
//                    (mActivity as MainActivity).hideOrShowBottomNav(0)
                } else {// 滑到其他页面的时候要显示tab和nav
//                    showTab()
//                    (mActivity as MainActivity).hideOrShowBottomNav(1)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataState = getFragmentViewModelProvider(this)[DataState::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_data,container,false)

        dataBinding = FragmentDataBinding.bind(view)
        dataBinding?.vm = dataState
        dataBinding?.click = ClickProxy()
        dataBinding?.lifecycleOwner = viewLifecycleOwner

        viewPager = view.findViewById(R.id.data_pager)
        tabLayout = view.findViewById(R.id.data_tab)

        viewPagerAdapter = DataPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        val tabList = listOf(getString(R.string.tab_map),getString(R.string.tab_palette),getString(R.string.tab_tip),getString(R.string.tab_other))
        val iconList = listOf<Int>(R.drawable.ic_map,R.drawable.ic_color_broard,
            R.drawable.ic_tag,R.drawable.ic_about)
        TabLayoutMediator(tabLayout,viewPager){tab, position ->
            tab.setIcon(iconList[position])
            tab.text = tabList[position]
        }.attach()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager.registerOnPageChangeCallback(viewPagerCallback)

        // 在这里进行liveData的监听
    }

    inner class ClickProxy {
        fun doMyJob(){
            Log.i(TAG, "doMyJob: ")
        }
    }

    /**
     * @param action 隐藏:0 显示:1
     * */
    fun hideOrShowTab(action :Int){
        when(action){
            0 ->{
                hideTab()
            }
            1 ->{
                showTab()
            }
        }
    }
    private fun hideTab(){
        dataBinding?.dataTab?.apply {
            clearAnimation()
            animate().translationY(-this.height.toFloat()).duration = 200
        }
    }

    private fun showTab(){
        dataBinding?.dataTab?.apply {
            clearAnimation()
            animate().translationY(0f).duration = 200
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        viewPager.unregisterOnPageChangeCallback(viewPagerCallback)
        dataBinding = null
    }
}