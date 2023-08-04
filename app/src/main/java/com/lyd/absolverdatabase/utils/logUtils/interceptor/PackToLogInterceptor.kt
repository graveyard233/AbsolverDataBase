package com.lyd.absolverdatabase.utils.logUtils.interceptor

import com.lyd.absolverdatabase.utils.logUtils.Chain
import com.lyd.absolverdatabase.utils.logUtils.Interceptor
import com.lyd.absolverdatabase.utils.logUtils.logExt.Log
import java.util.UUID

/**
 * 包装的中间层拦截器
 * */
class PackToLogInterceptor : Interceptor<Any>() {
    override fun log(tag: String, message: Any, priority: Int, chain: Chain, vararg args: Any) {
        if (isLoggable(message) && priority >= android.util.Log.INFO){// 只有log等级大于等于logi的才能发射
            // 把每条日志包装成带唯一标识符的 Log
            val log = Log(UUID.randomUUID().toString(), message)
            // 将 Log 沿责任链传递
            chain.proceed(tag, log, priority, args)
        }
    }
}