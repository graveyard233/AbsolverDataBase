package com.lyd.absolverdatabase

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.lyd.absolverdatabase.bridge.data.repository.BilibiliRepository
import com.lyd.absolverdatabase.bridge.data.repository.DeckRepository
import com.lyd.absolverdatabase.bridge.data.repository.database.db.AppDatabase
import com.lyd.architecture.utils.Utils
import com.tencent.mmkv.MMKV

class App : Application(), ViewModelStoreOwner {

    private var mAppViewModelStore: ViewModelStore? = null
    private var mFactory: ViewModelProvider.Factory? = null

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    val bilibiliRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        BilibiliRepository(database.videoDao())
    }

    val deckRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckRepository(database.testDao())
    }

    override fun onCreate() {
        super.onCreate()

        Utils.init(this)
        mAppViewModelStore = ViewModelStore()

        val path = MMKV.initialize(this)

//        // 这里必须初始化一下，是为了保证播放音乐管理类（PlayerManager.java） 不会为null，从而不引发空指针异常
//        PlayerManager.instance.init(this)

    }

    // 专门给 BaseActivity 与 BaseFragment 用的
    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            (activity.applicationContext as App),
            (activity.applicationContext as App).getAppFactory(activity) !!
        )
    }

    // AndroidViewModelFactory 工程是为了创建ViewModel，给上面的getAppViewModelProvider函数用的
    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory? {
        val application = checkApplication(activity)
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory
    }

    // 监测下 Activity是否为null
    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    // 监测下 Activity是否为null
    private fun checkActivity(fragment: Fragment): Activity? {
        return fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    // TODO 暴露出去 给外界用
    // 此函数只给 NavHostFragment 使用
    override fun getViewModelStore(): ViewModelStore = mAppViewModelStore !!
}