package com.lyd.absolverdatabase

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.MainActivityViewModel
import com.lyd.absolverdatabase.databinding.ActivityMainBinding
import com.lyd.absolverdatabase.ui.base.BaseActivity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    companion object{
        private const val TAG = "MainActivity"

        @JvmStatic
        private val rootList = listOf("com.lyd.absolverdatabase:id/learnFragment","com.lyd.absolverdatabase:id/dataFragment",
        "com.lyd.absolverdatabase:id/deckFragment","com.lyd.absolverdatabase:id/settingFragment")
    }

    private var mainBinding : ActivityMainBinding ? = null
    var mainActivityViewModel: MainActivityViewModel? = null

    private var backTime : Long = 0

    private var navController : NavController ?= null

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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        changeToolbar()

        mainBinding?.mainBottomNav?.setupWithNavController(navController!!)
        mainBinding?.mainRailNav?.setupWithNavController(navController!!)// 横向布局

    }

    override fun onDestroy() {
        super.onDestroy()
        mainBinding = null
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

    // 接管返回键，回到栈底时第二次才退出
    override fun onBackPressed() {
        if (!SettingRepository.autoSaveDeckWhenExitDeckEdit // 自动保存就话就不用拦截了
            && navController!!.currentDestination!!.label == getString(R.string.label_deckEditFragment)){
            // 如果是在deckEdit中返回，则需要判断是否需要询问保存
            if (mSharedViewModel.hashDeckBeenEdited){
                llog.d(msg = "当前界面是 deckEdit，卡组被编辑过，需要拦截返回")
                runBlocking {
                    mSharedViewModel.needShowSaveDialogWhenExitDeckEdit.emit(true)
                }
                return
            } else{
                llog.d(msg = "当前界面是 deckEdit,卡组还没被编辑过，放行")
            }
        }
        if (rootList.contains(navController!!.currentDestination!!.displayName)){
            // 已经回退到每一个栈的根部fragment了
            if (this.backTime != 0L && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.backTime) <= 1.5.toLong()){
//                Log.i(TAG, "onBackPressed: 这里应该退出了")
//                super.onBackPressed()
                moveTaskToBack(true)
                return
            } else {
//                Log.i(TAG, "onBackPressed: ${navController!!.currentDestination!!.displayName}")
                showShortToast(getString(R.string.press_one_more_time_for_finish))
                this.backTime = System.currentTimeMillis()
                return
            }
        }

        if (!navController!!.popBackStack()){
            finish()
        }

    }
    /**
     * @param action 隐藏:0 显示:1
     * */
    fun hideOrShowBottomNav(action :Int){
        when(action){
            0 ->{
                hideBottomNav()
            }
            1 ->{
                showBottomNav()
            }
        }
    }

    private fun hideBottomNav(){
        mainBinding?.mainBottomNav?.apply {
            clearAnimation()
            animate().translationY(this.height.toFloat()).duration = 200
        }
    }

    private fun showBottomNav(){
        mainBinding?.mainBottomNav?.apply {
            clearAnimation()
            animate().translationY(0f).duration = 200
        }
    }

    fun changeToolbar(){
        if (SettingRepository.isUseToolbar){
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.learnFragment,R.id.dataFragment,R.id.deckFragment,R.id.settingFragment))
            mainBinding?.apply {
                mainToolbar?.apply {
                    visibility = View.VISIBLE
                    setupWithNavController(navController!!,appBarConfiguration)
                }

                constraintLayout?.apply {
                    val tempLp :CoordinatorLayout.LayoutParams? = layoutParams as CoordinatorLayout.LayoutParams?
                    tempLp?.behavior = ScrollingViewBehavior()
                }
            }
        } else {
            mainBinding?.apply {
                mainToolbar?.visibility = View.GONE
                constraintLayout?.apply {
                    val tempLp :CoordinatorLayout.LayoutParams? = constraintLayout.layoutParams as CoordinatorLayout.LayoutParams?
                    tempLp?.behavior = null
                }
            }
        }
    }

}