package com.lyd.absolverdatabase.ui.page

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.ui.base.BaseFragment

class MapFragment :BaseFragment(){

    private lateinit var map :SubsamplingScaleImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_map,container,false)

        // 这里完成控件的初始化
        view.apply {
            map = findViewById(R.id.map_photo)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            map.setImage(ImageSource.resource(R.drawable.map_big))
            map.setOnStateChangedListener(object : SubsamplingScaleImageView.OnStateChangedListener{

                private var oldScale = map.minScale

                override fun onScaleChanged(newScale: Float, origin: Int) {
                    // 应该判断newScale和上一次比较来判断是缩小还是放大
                    (mActivity as MainActivity).hideOrShowBottomNav(if (newScale > oldScale) 0 else 1)
                    oldScale = newScale
                }

                override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                    // 不知道是干啥的，不用管
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "onViewCreated: ", e)
        }

        // 这里完成数据的监听
    }
}