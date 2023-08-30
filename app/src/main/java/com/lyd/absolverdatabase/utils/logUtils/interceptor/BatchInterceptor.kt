package com.lyd.absolverdatabase.utils.logUtils.interceptor

import android.os.SystemClock
import com.lyd.absolverdatabase.utils.logUtils.logExt.Log
import com.lyd.absolverdatabase.utils.logUtils.Chain
import com.lyd.absolverdatabase.utils.logUtils.Interceptor
import com.lyd.absolverdatabase.utils.logUtils.logExt.singleLogDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BatchInterceptor(
    private val size :Int,
    private val waitTime :Long = 10_000L
) : Interceptor<Any>() {
    private val list = mutableListOf<Log<*>>()
    private var lastFlushTime = 0L
    private val scope = CoroutineScope(SupervisorJob())
    private var flushJob: Job? = null


    override fun log(tag: String, message: Any, priority: Int, chain: Chain, args :List<Any>?) {
        if (isLoggable(message)){
            list.add(message as Log<*>)
            if (isOkFlush()) {
                flush(chain, tag, priority)
            } else {
                flushJob = delayFlush(chain, tag, priority)
            }
        }
    }

    private fun isOkFlush() = lastFlushTime != 0L && SystemClock.elapsedRealtime() - lastFlushTime >= waitTime || list.size >= size

    private fun flush(chain: Chain, tag: String, priority: Int){
        chain.proceed(tag,list,priority,null)

        list.clear()
        lastFlushTime = SystemClock.elapsedRealtime()
    }

    private fun delayFlush(chain: Chain, tag: String, priority: Int) = scope.launch(
        singleLogDispatcher
    ) {
        val delayTime = if (lastFlushTime == 0L) waitTime else waitTime - (SystemClock.elapsedRealtime() - lastFlushTime)
        delay(delayTime)
        flush(chain, tag, priority)
    }
}