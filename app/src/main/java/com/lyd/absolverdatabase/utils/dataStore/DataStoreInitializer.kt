package com.lyd.absolverdatabase.utils.dataStore

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.UseTheme
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.isNightMode
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 用来初始化context的东西，现在没放在manifest里面用startup初始化，放在App中初始化
 * */
class DataStoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        IDataStoreOwner.application = context as Application
        /*CoroutineScope(Dispatchers.IO + SupervisorJob()).launch*/runBlocking {// 注意，这里的初始化时间在Application的OnCreate之后，想要更快得写在App里面
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
                val tempUseCNEditionMod = async {
                    SettingRepository.isUseCNEditionModPreference.getOrDefault()
                }
                val tempUseNightMode = async {
                    SettingRepository.useNightModePreference.getOrDefault()
                }
                val tempUseWhatTheme = async {
                    SettingRepository.useWhatThemePreference.getOrDefault()
                }
                SettingRepository.isDialogGaussianBlur = tempGaussianBlur.await()
                SettingRepository.isNeedAskBeforeImport = tempAskBeforeImport.await()
                SettingRepository.isUseCNEditionMod = tempUseCNEditionMod.await()
                SettingRepository.useNightMode = tempUseNightMode.await()
                SettingRepository.useWhatTheme = tempUseWhatTheme.await()

                when(SettingRepository.useNightMode){
                    true -> {
                        if (!context.isNightMode()){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                    false -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }

            }
            Log.i("DataStoreInitializer", "create: 初始化配置参数 async 处理时间:$timeCost1")

        }
        when(SettingRepository.useWhatTheme){
            UseTheme.DefaultId ->{
                Log.i("DataStoreInitializer", "create: 使用默认主题")
            }
            UseTheme.WallpaperId ->{
                DynamicColors.applyToActivitiesIfAvailable(IDataStoreOwner.application)
            }
            UseTheme.RedId ->{
                DynamicColors.applyToActivitiesIfAvailable(IDataStoreOwner.application, DynamicColorsOptions.Builder().setThemeOverlay(
                    R.style.Theme_AbsolverDatabase_Red).build())
            }
            UseTheme.YellowId ->{
                DynamicColors.applyToActivitiesIfAvailable(IDataStoreOwner.application, DynamicColorsOptions.Builder().setThemeOverlay(
                    R.style.Theme_AbsolverDatabase_Gold).build())
            }
            UseTheme.BlueId ->{
                DynamicColors.applyToActivitiesIfAvailable(IDataStoreOwner.application, DynamicColorsOptions.Builder().setThemeOverlay(
                    R.style.Theme_AbsolverDatabase_Blue).build())
            }
            UseTheme.GreenId ->{
                DynamicColors.applyToActivitiesIfAvailable(IDataStoreOwner.application, DynamicColorsOptions.Builder().setThemeOverlay(
                    R.style.Theme_AbsolverDatabase_Green).build())
            }
        }
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}