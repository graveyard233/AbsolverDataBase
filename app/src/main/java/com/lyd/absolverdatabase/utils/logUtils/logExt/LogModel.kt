package com.lyd.absolverdatabase.utils.logUtils.logExt

/**
 * An inner model wrap [T] with an unique id
 */
data class Log<T>(val id: String, val data: T)