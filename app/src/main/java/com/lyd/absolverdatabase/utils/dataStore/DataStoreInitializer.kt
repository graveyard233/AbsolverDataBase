package com.lyd.absolverdatabase.utils.dataStore

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.startup.Initializer
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 用来初始化context的东西，现在放在manifest里面用startup初始化
 * */
class DataStoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        IDataStoreOwner.application = context as Application
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {// 注意，这里的初始化时间在Application的OnCreate之后，想要更快得写在App里面
            val timeCost1 = measureTimeMillis {

                val tempGaussianBlur = async {
                    if (Build.VERSION.SDK_INT < 31){
                        false
                    } else{
                        SettingRepository.isDialogGaussianBlurPreference.getOrDefault()
                    }
                }
                val tempAskBeforeImport = async {
                    SettingRepository.isNeedAskBeforeImportPreference.getOrDefault()
                }
                val tempShowStyleIconInMoveMsg = async {
                    SettingRepository.isNeedShowStyleIconInMoveMsgPreference.getOrDefault()
                }

                SettingRepository.isDialogGaussianBlur = tempGaussianBlur.await()
                SettingRepository.isNeedAskBeforeImport = tempAskBeforeImport.await()
                SettingRepository.isNeedShowStyleIconInMoveMsg = tempShowStyleIconInMoveMsg.await()

            }
            Log.i("DataStoreInitializer", "create: 初始化配置参数 async 处理时间:$timeCost1")

        }
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}