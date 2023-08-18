package com.lyd.absolverdatabase.utils.crashUtils

import android.content.Context
import android.os.Build
import com.lyd.absolverdatabase.BuildConfig
import com.lyd.absolverdatabase.utils.logUtils.LLog
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object CrashHelperUtil {

    private val FILE_SUFFIX :String = ".txt"
    private val formatStr = "yyyy-MM-dd_HH:mm:ss"
    private val dataFormat :SimpleDateFormat = SimpleDateFormat(formatStr, Locale.CHINA)

    private var crashTime = "crashTime"
    private var crashHead = "crashHead"
    private var versionName = "versionName"
    private var versionCode = "versionCode"

    private fun initCrashHead(){
        crashTime = dataFormat.format(Date(System.currentTimeMillis()))

        versionName = BuildConfig.VERSION_NAME
        versionCode = BuildConfig.VERSION_CODE.toString()

//        val tm = Utils.getApp().getSystemService(TelecomManager::class.java)


        crashHead = "AppId:${BuildConfig.APPLICATION_ID}" +
                "\nBuildType:${BuildConfig.BUILD_TYPE}" +
                "\n应用版本:$versionName-$versionCode" +
                "\n崩溃时间:$crashTime" +
                "\n设备品牌:${Build.BRAND}" +
                "\n手机型号:${Build.MODEL}" +
                "\n系统版本:${Build.VERSION.SDK_INT}" +
                "\n"

    }
    private fun reInitHead(){
        crashTime = "crashTime"
        crashHead = "crashHead"
        versionName = "versionName"
        versionCode = "versionCode"
    }

    fun dumpExceptionToFile(context: Context,ex :Throwable){
        var file :File? = null
        var pw :PrintWriter? = null
        try {
            val crashDir = File(context.getExternalFilesDir("crash")!!.absolutePath)
            if (!crashDir.exists()){
                if (!crashDir.mkdirs()){
                    return
                }
            }

            initCrashHead()

            val fileName = "V${versionName}_$crashTime$FILE_SUFFIX"

            file = File(crashDir,fileName)

            if (!file.exists()){
                val isCreate = file.createNewFile()
                if (!isCreate){
                    return
                }
            }

            pw = PrintWriter(BufferedWriter(FileWriter(file)))

            pw.println(crashHead)

            ex.printStackTrace(pw)

            LLog.d(msg = "保存的崩溃日志路径:${file.path}")

        }catch (exception :Exception){
            LLog.e(tag = "saveCrashFileFalse", msg = exception)
        } finally {
            reInitHead()
            pw?.close()
        }
    }
}