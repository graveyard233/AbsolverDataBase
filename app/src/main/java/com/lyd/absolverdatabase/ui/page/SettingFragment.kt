package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.SettingViewModel
import com.lyd.absolverdatabase.databinding.FragmentSettingBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class SettingFragment : BaseFragment() {

    private var settingBinding : FragmentSettingBinding? = null
    private var settingViewModel : SettingViewModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingViewModel = getFragmentViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_setting,container,false)

        settingBinding = FragmentSettingBinding.bind(view)
        settingBinding?.vm = settingViewModel
        settingBinding?.click = ClickProxy()
        settingBinding?.lifecycleOwner = viewLifecycleOwner

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
        settingBinding = null
    }
}