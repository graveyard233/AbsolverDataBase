package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.LearnViewModel
import com.lyd.absolverdatabase.databinding.FragmentLearnBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class LearnFragment : BaseFragment() {

    companion object{
        private const val TAG :String = "LearnFragment"
    }

    private var learnBinding : FragmentLearnBinding ? = null
    private var learnViewModel : LearnViewModel ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化viewModel
        learnViewModel = getFragmentViewModelProvider(this)[LearnViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载界面
        val view:View = inflater.inflate(R.layout.fragment_learn,container,false)

        // 绑定binding，设置vm，设置点击事件
        learnBinding = FragmentLearnBinding.bind(view)
        learnBinding?.vm = learnViewModel
        learnBinding?.click = ClickProxy()
        learnBinding?.lifecycleOwner = viewLifecycleOwner // 注意，这里一定是要view的生命周期，防止被回收时还出问题

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
        learnBinding = null
    }
}