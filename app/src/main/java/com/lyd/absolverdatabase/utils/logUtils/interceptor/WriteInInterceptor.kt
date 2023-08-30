package com.lyd.absolverdatabase.utils.logUtils.interceptor

import com.lyd.absolverdatabase.utils.logUtils.Chain
import com.lyd.absolverdatabase.utils.logUtils.Interceptor
import com.lyd.absolverdatabase.utils.logUtils.LLog
import com.lyd.absolverdatabase.utils.logUtils.logExt.Log
import com.lyd.absolverdatabase.utils.logUtils.logExt.LogItem
import com.lyd.absolverdatabase.utils.logUtils.logWrite.BaseLogWriter

// 以后这个只接收某些类型 只写入某些类型的信息
class WriteInInterceptor(
    private val logWriter: BaseLogWriter
) : Interceptor<LogItem>() {
    override fun log(tag: String, log: LogItem, priority: Int, chain: Chain, args :List<Any>?) {
        if (
            isLoggable(log) && /*priority >= android.util.Log.INFO*/
            (LLog.minWritePriority <= priority && priority <= LLog.maxWritePriority) &&
            !(LLog.minWritePriority == LLog.NONE && LLog.maxWritePriority == LLog.NONE)
            )
        {
            // 将日志写入本地文件
//            android.util.Log.i("WriteInInterceptor", "log: 写入本地 $log")
            logWriter.writeIn(log)
        }
    }
}