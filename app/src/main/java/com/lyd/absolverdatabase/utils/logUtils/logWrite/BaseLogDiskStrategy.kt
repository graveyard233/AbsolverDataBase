package com.lyd.absolverdatabase.utils.logUtils.logWrite

import java.io.File

// https://github.com/elvishew/xLog/blob/master/xlog/src/main/java/com/elvishew/xlog/printer/file/FilePrinter.java
// https://github.com/oi-october/OTLogger/blob/master/otLogger/src/main/java/com/october/lib/logger/disk/BaseLogDiskStrategy.kt
// https://github.com/orhanobut/logger/blob/master/logger/src/main/java/com/orhanobut/logger/DiskLogStrategy.java
abstract class BaseLogDiskStrategy(
    val logDirPath :String
) {
    companion object{
        private const val logPrefix = "Log_"
        private const val logSuffix = ".log"
    }

    private val currentLogFilePath :String? = null

    internal fun internalGetLogWritePath() :String{
        val logDirFile = File(logDirPath)
        if (!logDirFile.exists() || !logDirFile.isDirectory){
            logDirFile.mkdirs() // 一定能成功，因为是操作应用专属的文件夹，不会有错误，且一定是能读能写，不会有权限问题
        }
        return getLogWritePath()
    }
}