package com.lyd.absolverdatabase.utils.logUtils.logWrite

import com.lyd.absolverdatabase.utils.logUtils.LLog
import com.lyd.absolverdatabase.utils.logUtils.logExt.LogItem
import java.io.File
import java.io.FilenameFilter
import java.text.SimpleDateFormat
import java.util.Locale

class FileLogDiskStrategyImpl(
    private val logDirectory :String,
    val logFileStoreSizeOfMB :Int = 5
): BaseLogDiskStrategy(logDirectory) {

    private val logFileNameDateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA)
    private var currentFilePathCache :FilePathCache? = null


    override fun isLogFilePathAvailable(logFilePath: String, logBody: String): Boolean {
        return currentFilePathCache?.isMatch(logFilePath) == true
    }

    /**判断是否允许生成日志文件*/
    override fun isAllowCreateLogFile(logTime: Long): Boolean {
        checkAndClearLogFile()
        return true
    }

    override fun createLogFile(logItem: LogItem): String {
        var fileName = ""
        val currentLogFileCacheSize = currentFilePathCache?.getCurrentSize()
        if (currentLogFileCacheSize != null && currentLogFileCacheSize > logFileStoreSizeOfMB * 1024 * 1024){
            fileName = getFileName(System.currentTimeMillis())
        } else {
            val fileArray = File(logDirPath).listFiles(FilenameFilter { _, name ->
                return@FilenameFilter (name.startsWith(logPrefix) && name.endsWith(logSuffix))
            })

            if (!fileArray.isNullOrEmpty()){
                var fileList = fileArray.sortedBy {
                    it.name
                }
                val lastFile = fileList.last()
                if (lastFile.length() < logFileStoreSizeOfMB * 1024 *1024){
                    fileName = lastFile.name
                }
            }
            if (fileName.isEmpty()){
                fileName = getFileName(System.currentTimeMillis())
            }
        }

        val path = logDirPath + File.separator + fileName
        LLog.d(msg = "create log file = $path")
        val tempFilePath = FilePathCache(logFileStoreSizeOfMB * 1024 * 1024L,path)
        currentFilePathCache = tempFilePath
        return tempFilePath.filePath
    }

    private fun checkAndClearLogFile() {
//        TODO("Not yet implemented")
        val logDirFile = File(logDirPath)
        if (!logDirFile.exists() || !logDirFile.isDirectory){
            LLog.e(msg = "log dir not exit")
            return
        }
        val logFileArray = logDirFile.listFiles(FilenameFilter { _, name ->
            val nameStr = name.trim()
            if (nameStr.startsWith(logPrefix) && nameStr.endsWith(logSuffix)){
                return@FilenameFilter true
            }
            return@FilenameFilter false
        })
        var logList = logFileArray?.asList()
        if (logList.isNullOrEmpty()){
            return
        }
        logList = logList.sortedBy { it.name }
        var size = 0
        var outSizeIndex = -1
        for (i in logList.size -1 downTo 0){
            val logFile = logList[i]
        }
    }

    private fun getFileName(time :Long) :String{
        val tempTimeStr = logFileNameDateFormat.format(time)
        return "$logPrefix$tempTimeStr$logSuffix"
    }

    private class FilePathCache(val logFileMaxSize :Long,val filePath :String){
        private val MAX_RESET_COUNT = 50
        private var curResetCount = 0
        private var currentSize = -1L

        private val logFile by lazy {
            File(filePath)
        }

        init {
            if (logFile.exists() && logFile.isFile){
                currentSize = logFile.length()
            } else {
                currentSize = 0
            }
        }

        fun isMatch(logFilePath: String) :Boolean{
            if (curResetCount > MAX_RESET_COUNT || currentSize < 0){// 每50次，查一次文件大小
                curResetCount = 0
                currentSize = logFile.length()
            }
            curResetCount++
            // 当前文件大小 < 最大限制大小， 表示匹配
            return currentSize < logFileMaxSize && filePath == logFilePath
        }

        fun getCurrentSize() :Long {
            return currentSize
        }
    }
}