package com.lyd.absolverdatabase.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.databinding.FragmentTest1Binding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class Test1Fragment : BaseFragment() {

    private var test1Binding : FragmentTest1Binding ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_test1,container,false)

        test1Binding = FragmentTest1Binding.bind(view)

        return view
    }
}