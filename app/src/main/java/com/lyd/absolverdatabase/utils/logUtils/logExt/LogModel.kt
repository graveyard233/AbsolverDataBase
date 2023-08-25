package com.lyd.absolverdatabase.utils.logUtils.logExt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * An inner model wrap [T] with an unique id
 */
data class Log<T>(val id: String, val data: T)

@Parcelize
data class LogItem(val time :Long,val logLevel :Int,val tag :String,val data :String) : Parcelable