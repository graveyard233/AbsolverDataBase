package com.lyd.absolverdatabase.utils.logUtils.logWrite

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.lyd.absolverdatabase.utils.logUtils.logExt.LogItem

abstract class BaseLogWriter {
    companion object{
        private const val ITEMS_KEY = "LogItems"
    }


    open class WriteHandler(
        looper: Looper,
        val logDiskStrategy: BaseLogDiskStrategy
    ) :Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val logItems :List<LogItem> = msg.data.getParcelableArrayList(ITEMS_KEY)!!

        }

        fun log(items :List<LogItem>){
            if (items.isEmpty())
                return

            val logField = log
        }
    }
}