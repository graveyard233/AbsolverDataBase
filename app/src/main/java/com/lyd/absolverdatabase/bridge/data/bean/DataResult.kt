package com.lyd.absolverdatabase.bridge.data.bean

/**
 * 多状态函数返回值
 * */
sealed class DataResult<out T>{
    data class Success<out T>(val data: T): DataResult<T>()
    data class Error(val error:String): DataResult<Nothing>()
}

