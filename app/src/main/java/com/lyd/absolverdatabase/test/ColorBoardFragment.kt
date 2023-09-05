package com.lyd.absolverdatabase.test

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.ClipUtil

class ColorBoardFragment :BaseFragment(){

    private lateinit var scroll :NestedScrollView
    private lateinit var flexboxLayout: FlexboxLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_color_board,container,false)

        scroll = view.findViewById(R.id.color_board_scroll)
        flexboxLayout = view.findViewById(R.id.color_board_flexbox)

        val tempLongClickListener = View.OnLongClickListener{textView->
            if (textView.background is ColorDrawable){
                val tempCD = textView.background as ColorDrawable
                val hexColor = String.format("#%06X",0xFFFFFF and tempCD.color)
                Snackbar.make(flexboxLayout,"${(textView as TextView).text} -> $hexColor",Snackbar.LENGTH_SHORT)
                    .setAction(R.string.copy){
                        ClipUtil.copyText(hexColor)
                    }.show()
            }
            return@OnLongClickListener true
        }
        flexboxLayout.forEach {
            if (it is TextView){
                it.setOnLongClickListener(tempLongClickListener)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        scroll.setOnScrollChangeListener { _, l, t, oldl, oldt ->
//            if (oldt < t && ((t - oldt) > 15)) {// 向上滑动
//                (mActivity as MainActivity).hideOrShowBottomNav(0)
//                (parentFragment as DataFragment).hideOrShowTab(0)
//            } else if (oldt > t && (oldt - t) > 15){// 向下滑动
//                (mActivity as MainActivity).hideOrShowBottomNav(1)
//            }
//        }
    }
}