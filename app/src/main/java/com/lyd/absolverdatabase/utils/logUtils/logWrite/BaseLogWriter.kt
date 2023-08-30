package com.lyd.absolverdatabase.utils.logUtils.logWrite

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.lyd.absolverdatabase.utils.logUtils.LLog
import com.lyd.absolverdatabase.utils.logUtils.logExt.LogItem
import java.io.File

abstract class BaseLogWriter {
    companion object{
        private const val ITEMS_WHAT = 0
        private const val ITEMS_KEY = "LogItems"
        private const val ITEM_WHAT = 1
        private const val ITEM_KEY = "LogItem"
    }

    @Volatile
    private var handler :WriteHandler ?= null

    fun writeIn(logItem: LogItem){
        getWriterHandler().sendMessage(getWriterHandler().obtainMessage(ITEM_WHAT).apply {
            data.putParcelable(ITEM_KEY,logItem)
        })
    }

    //日志输出格式
    abstract fun getLogcatFormatStrategy(): BaseFormatStrategy

    //获取日志文件夹管理策略
    abstract fun getLogDiskStrategy(): BaseLogDiskStrategy


    private fun getWriterHandler() :WriteHandler{
        if (handler == null){
            synchronized(this){
                if (handler == null){
                    val handlerThread = HandlerThread("LLog")
                    handlerThread.start()
                    handler = WriteHandler(handlerThread.looper,getLogDiskStrategy(),getLogcatFormatStrategy())
                }
            }
        }
        return handler!!
    }


    open class WriteHandler(
        looper: Looper,
        private val logDiskStrategy: BaseLogDiskStrategy,
        private val logFormatStrategy :BaseFormatStrategy
    ) :Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                ITEM_WHAT ->{
                    val logItem :LogItem? = msg.data.getParcelable(ITEM_KEY)
                    logItem?.let {
                        logWrite(logItem)
                    }
                }
                ITEMS_WHAT ->{
                    val logItems :List<LogItem>? = msg.data.getParcelableArrayList(ITEMS_KEY) // 留给以后的批处理用的
                    logItems?.let {
                        logWrite(it)
                    }
                }
            }


        }

        private fun logWrite(item :LogItem){
            val logFilePath = logDiskStrategy.internalGetLogWritePath(logItem = item)
            if (logFilePath.isEmpty()){
                return
            }
            val logFile = File(logFilePath)
            try {
                if (!logFile.exists() || !logFile.isFile){
                    if (!logFile.createNewFile()){
                        return
                    }
                }
                logFile.appendText(logFormatStrategy.format(item.time,item.tag,item.logLevel,item.data))
            } catch (e :Exception){
                LLog.e(msg = e.message!!)
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
        fun logWrite(items :List<LogItem>){
            if (items.isEmpty())
                return
            val logFilePath = logDiskStrategy.internalGetLogWritePath(items[0])
            if (logFilePath.isEmpty())
                return
            val logFile = File(logFilePath)
            try {
                if (!logFile.exists() || !logFile.isFile){
                    if (!logFile.createNewFile()){
                        return
                    }
                }
                items.forEachIndexed{index: Int, item: LogItem ->
                    logFile.appendText(logFormatStrategy.format(item.time,item.tag,item.logLevel,item.data))
                }
            } catch (e :Exception){
                LLog.e(msg = e.message!!)
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }
}