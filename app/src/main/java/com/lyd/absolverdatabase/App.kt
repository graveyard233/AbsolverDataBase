package com.lyd.absolverdatabase

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.lyd.absolverdatabase.bridge.data.repository.*
import com.lyd.absolverdatabase.bridge.data.repository.database.db.AppDatabase
import com.lyd.absolverdatabase.utils.crashUtils.CrashHelperUtil
import com.lyd.absolverdatabase.utils.crashUtils.ICrashCallback
import com.lyd.absolverdatabase.utils.crashUtils.JavaCrashHandler
import com.lyd.absolverdatabase.utils.logUtils.LLog
import com.lyd.absolverdatabase.utils.logUtils.interceptor.LinearInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.LogcatInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.PackToLogInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.WriteInInterceptor
import com.lyd.architecture.utils.Utils
import com.tencent.mmkv.MMKV
import java.util.Locale

class App : Application(), ViewModelStoreOwner {

    companion object{
        private const val TAG = "App"
    }

    private var mAppViewModelStore: ViewModelStore? = null
    private var mFactory: ViewModelProvider.Factory? = null

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    val bilibiliRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        BilibiliRepository(database.videoDao())
    }

    val deckRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckRepository(database.deckDao(),database.moveJsDao(),database.moveOriginDao(),database.moveCEDao())
    }

    val deckEditRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        DeckEditRepository(database.deckDao(),database.moveOriginDao(),database.moveCEDao())
    }

    val moveRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MoveRepository(database.moveOriginDao(), moveCEDAO = database.moveCEDao())
    }

    val settingDatabaseRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        SettingDatabaseRepository(moveOriginDAO = database.moveOriginDao(), moveCEDAO = database.moveCEDao())
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate: 初始化Utils")
        Utils.init(this)
        mAppViewModelStore = ViewModelStore()

        val path = MMKV.initialize(this)

        val curLanguage = if (AppCompatDelegate.getApplicationLocales().isEmpty){
            Log.i(TAG, "onCreate: AppCompatDelegate.getApplicationLocales().isEmpty")
            Locale.getDefault().toLanguageTag()
        } else {
            Log.i(TAG, "onCreate: use AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()")
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: "error"
        }

        LLog.apply {
            setDebug(isLoggable = true, methodNameEnable = true)
            addInterceptor(LogcatInterceptor())
            addInterceptor(LinearInterceptor().apply { isLoggable = {
                !BuildConfig.DEBUG
            }})
            addInterceptor(PackToLogInterceptor())
            addInterceptor(WriteInInterceptor().apply {
                isLoggable = {
                    it.data is String
                }
            })
        }
        LLog.e(msg = "LLog的打印等级是-> ${LLog.minPrintPriority} ~ ${LLog.maxPrintPriority} 写入等级是-> ${LLog.minWritePriority} ~ ${LLog.maxWritePriority}")
        LLog.i(msg = "onCreate: this language -> $curLanguage")
        // 把初始化阶段的代码写在了manifest，交给startup来处理，不知道从哪里引入了startup的库，可能依赖混乱了吧，假如不想写在manifest，可以用下面的手动初始化也行，目的就是要拿到application
//        AppInitializer.getInstance(applicationContext).initializeComponent(DataStoreInitializer::class.java)


        JavaCrashHandler.get().init(this,object :ICrashCallback{
            override fun onCrash(ex: Throwable) {
                LLog.e(tag = "Crash", msg = ex)

                if (SettingRepository.isRecordCrashMsg){
                    CrashHelperUtil.dumpExceptionToFile(this@App,ex)
                }
            }
        })
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