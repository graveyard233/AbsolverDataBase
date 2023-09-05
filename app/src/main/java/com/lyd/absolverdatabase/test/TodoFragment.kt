package com.lyd.absolverdatabase.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.ui.base.BaseFragment

class TodoFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_todo,container,false)

        return view
    }
}