package com.lyd.absolverdatabase.utils.logUtils

import android.content.Context
import androidx.startup.Initializer
import com.lyd.absolverdatabase.BuildConfig

import com.lyd.absolverdatabase.utils.logUtils.interceptor.LinearInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.LogcatInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.PackToLogInterceptor
import com.lyd.absolverdatabase.utils.logUtils.interceptor.WriteInInterceptor
import com.lyd.absolverdatabase.utils.logUtils.logWrite.FileLogDiskStrategyImpl
import com.lyd.absolverdatabase.utils.logUtils.logWrite.LogDefaultWriter
import com.lyd.absolverdatabase.utils.logUtils.logWrite.LogWriteDefaultFormatStrategy
import kotlin.system.measureTimeMillis

class LLogInitializer : Initializer<LLog> {
    override fun create(context: Context): LLog {
        val timeCost = measureTimeMillis {
            LLog.apply {
                setDebug(isLoggable = true, methodNameEnable = true)
                addInterceptor(LogcatInterceptor())
                addInterceptor(LinearInterceptor(),isLoggable = {
                    BuildConfig.DEBUG
                })
                addInterceptor(PackToLogInterceptor())
                addInterceptor(
                    WriteInInterceptor(
                        logWriter = LogDefaultWriter(
                            formatStrategy = LogWriteDefaultFormatStrategy(),
                            diskStrategy = FileLogDiskStrategyImpl(
                                logDirectory = context.getExternalFilesDir("log")!!.absolutePath,
                                logFileStoreSizeOfMB = 2,
                                logFileMaxNumber = 4
                            )
                        )
                    )
                )
            }
        }
        LLog.i(tag = javaClass.simpleName, msg = "---------LLog 初始化完成 耗时$timeCost--------------")
        return LLog
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList<Class<Initializer<*>>>()
    }
}