package com.lyd.absolverdatabase.bridge.data.bean

sealed class RepoResult<out T>
data class RpSuccess<out T>(val data: T): RepoResult<T>()
data class RpError(val error:String): RepoResult<Nothing>()
data class RpEmpty(val msg :String) :RepoResult<Nothing>()
