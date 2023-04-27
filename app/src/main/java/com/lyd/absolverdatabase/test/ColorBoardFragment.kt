package com.lyd.absolverdatabase.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.page.DataFragment

class ColorBoardFragment :BaseFragment(){

    private lateinit var scroll :NestedScrollView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_color_board,container,false)

        scroll = view.findViewById(R.id.color_board_scroll)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scroll.setOnScrollChangeListener { _, l, t, oldl, oldt ->
            if (oldt < t && ((t - oldt) > 15)) {// 向上滑动
                (mActivity as MainActivity).hideOrShowBottomNav(0)
                (parentFragment as DataFragment).hideOrShowTab(0)
            } else if (oldt > t && (oldt - t) > 15){// 向下滑动
                (mActivity as MainActivity).hideOrShowBottomNav(1)
            }
        }
    }
}