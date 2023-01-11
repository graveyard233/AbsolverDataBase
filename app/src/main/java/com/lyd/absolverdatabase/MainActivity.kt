package com.lyd.absolverdatabase

import android.os.Bundle

import androidx.core.view.WindowCompat

import androidx.databinding.DataBindingUtil
import com.lyd.absolverdatabase.bridge.state.MainActivityViewModel
import com.lyd.absolverdatabase.databinding.ActivityMainBinding
import com.lyd.absolverdatabase.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    private var mainBinding : ActivityMainBinding ? = null
    var mainActivityViewModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        mainActivityViewModel = getActivityViewModelProvider(this)[MainActivityViewModel::class.java]
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mainBinding?.lifecycleOwner = this
        mainBinding?.vm = mainActivityViewModel


        // 共享 （观察）
        mSharedViewModel.activityCanBeClosedDirectly.observe(this, {
            // 先不写，作用不大
        })

    }

    /**
     * 详情看：https://www.cnblogs.com/lijunamneg/archive/2013/01/19/2867532.html
     * 这个onWindowFocusChanged指的是这个Activity得到或者失去焦点的时候 就会call。。
     * 也就是说 如果你想要做一个Activity一加载完毕，就触发什么的话 完全可以用这个！！！
     *  entry: onStart---->onResume---->onAttachedToWindow----------->onWindowVisibilityChanged--visibility=0---------->onWindowFocusChanged(true)------->
     * @param hasFocus
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (!isListened) {
//            mSharedViewModel.timeToAddSlideListener.value = true // 触发改变
//            isListened = true
//        }
    }

    override fun onBackPressed() {
         super.onBackPressed()
//        mSharedViewModel.closeSlidePanelIfExpanded.value = true // 触发改变
    }

}