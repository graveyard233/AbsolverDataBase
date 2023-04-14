package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DataViewModel
import com.lyd.absolverdatabase.databinding.FragmentDataBinding
import com.lyd.absolverdatabase.ui.adapter.DataPagerAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment

class DataFragment : BaseFragment() {

    private var dataBinding : FragmentDataBinding ? = null
    private var dataViewModel : DataViewModel ?= null

    private lateinit var viewPagerAdapter: DataPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout :TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataViewModel = getFragmentViewModelProvider(this)[DataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_data,container,false)

        dataBinding = FragmentDataBinding.bind(view)
        dataBinding?.vm = dataViewModel
        dataBinding?.click = ClickProxy()
        dataBinding?.lifecycleOwner = viewLifecycleOwner

        viewPager = view.findViewById(R.id.data_pager)
        tabLayout = view.findViewById(R.id.data_tab)

        viewPagerAdapter = DataPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        val tabList = listOf(getString(R.string.tab_map),"其他")
        val iconList = listOf<Int>(R.drawable.ic_map,R.drawable.ic_tag)
        TabLayoutMediator(tabLayout,viewPager){tab, position ->
            tab.setIcon(iconList[position])
            tab.text = tabList[position]
        }.attach()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 在这里进行liveData的监听
    }

    inner class ClickProxy {
        fun doMyJob(){
            Log.i(TAG, "doMyJob: ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        dataBinding = null
    }
}