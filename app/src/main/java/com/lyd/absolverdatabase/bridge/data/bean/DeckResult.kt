package com.lyd.absolverdatabase.bridge.data.bean

sealed class DeckResult<out T>
data class Success<out T>(val data: T): DeckResult<T>()
data class Error(val error:String): DeckResult<Nothing>()
data class Empty(val msg :String) :DeckResult<Nothing>()
