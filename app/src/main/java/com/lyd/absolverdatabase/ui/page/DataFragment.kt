package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DataViewModel
import com.lyd.absolverdatabase.databinding.FragmentDataBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class DataFragment : BaseFragment() {

    companion object{
        private const val TAG :String = "DataFragment"
    }

    private var dataBinding : FragmentDataBinding ? = null
    private var dataViewModel : DataViewModel ?= null

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 在这里进行liveData的监听
    }

    inner class ClickProxy {
        fun doMyJob(){
            Log.i(TAG, "doMyJob: ")
            nav().navigate(R.id.action_dataFragment_to_test1Fragment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        dataBinding = null
    }
}