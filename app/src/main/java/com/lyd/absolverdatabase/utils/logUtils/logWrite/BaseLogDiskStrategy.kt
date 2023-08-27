package com.lyd.absolverdatabase.utils.logUtils.logWrite

import com.lyd.absolverdatabase.utils.logUtils.LLog
import com.lyd.absolverdatabase.utils.logUtils.logExt.LogItem
import java.io.File

// https://github.com/elvishew/xLog/blob/master/xlog/src/main/java/com/elvishew/xlog/printer/file/FilePrinter.java
// https://github.com/oi-october/OTLogger/blob/master/otLogger/src/main/java/com/october/lib/logger/disk/BaseLogDiskStrategy.kt
// https://github.com/orhanobut/logger/blob/master/logger/src/main/java/com/orhanobut/logger/DiskLogStrategy.java
abstract class BaseLogDiskStrategy(
    val logDirPath :String
) {
    companion object{
        internal const val logPrefix = "Log_"
        internal const val logSuffix = ".log"
    }

    private var currentLogFilePath :String? = null

    internal fun internalGetLogWritePath(logItem: LogItem) :String{
        val logDirFile = File(logDirPath)
        if (!logDirFile.exists() || !logDirFile.isDirectory){
            logDirFile.mkdirs() // 一定能成功，因为是操作应用专属的文件夹，不会有错误，且一定是能读能写，不会有权限问题
        }
        return getLogWritePath(logItem)
    }

    open fun getLogWritePath(logItem: LogItem):String{
        val path = logDirPath
        if (isLogFilePathAvailable(path,logItem.data)){
            return path
        } else {
            if (!isAllowCreateLogFile(logItem.time)){
                LLog.e(msg = "is not allow create log file")
                return ""
            }
            var path :String = createLogFile(logItem)

            currentLogFilePath = path
            return currentLogFilePath!!
        }
    }



    /**判断日志是否可以输出到文件中*/
    abstract fun isLogFilePathAvailable(logFilePath :String, logBody :String) :Boolean

    abstract fun isAllowCreateLogFile(logTime :Long) :Boolean

    abstract fun createLogFile(logItem: LogItem): String
}