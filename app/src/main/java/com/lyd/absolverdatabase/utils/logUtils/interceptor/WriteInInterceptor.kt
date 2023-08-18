package com.lyd.absolverdatabase.utils.logUtils.interceptor

import com.lyd.absolverdatabase.utils.logUtils.Chain
import com.lyd.absolverdatabase.utils.logUtils.Interceptor
import com.lyd.absolverdatabase.utils.logUtils.logExt.Log

// 以后这个只接收某些类型 只写入某些类型的信息
class WriteInInterceptor : Interceptor<Log<*>>() {
    override fun log(tag: String, log: Log<in Nothing>, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(log) && priority >= android.util.Log.INFO ){
            // 将日志写入本地文件
            android.util.Log.i("WriteInInterceptor", "log: 写入本地 $log")
        }
    }
}