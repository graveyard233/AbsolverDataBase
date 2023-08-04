package com.lyd.absolverdatabase.utils.logUtils

abstract class Interceptor<T> {
    // 日志处理逻辑
    abstract fun log(tag :String, message :T, priority :Int, chain: Chain, vararg args :Any)
    // 是否启动当前拦截器
    var isLoggable: (T) -> Boolean = { true }
}