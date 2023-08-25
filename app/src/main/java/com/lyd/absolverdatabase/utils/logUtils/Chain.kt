package com.lyd.absolverdatabase.utils.logUtils

import android.util.Log

class Chain(
    private val interceptors: List<Interceptor<in Nothing>>,
    private val index :Int = 0
) {
    /**
     * 责任链向后传递 Any 类型的日志
     * @param args 第一个位置放日志生成时间的时间戳 类型为[Long]
     * */
    fun proceed(tag :String, message :Any, priority :Int,vararg args :Any){
        val next = Chain(interceptors, index + 1)
        try {
            (interceptors.getOrNull(index) as? Interceptor<Any>)?.log(tag,message,priority,next,*args)
        } catch (e :Exception){
            Log.d("LLog", "Chain.proceed[tag, message, priority, args]: e=${e}")
        }
    }
}