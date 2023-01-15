package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.ArchiveData
import retrofit2.http.QueryMap

/**
 * 网络请求的标准接口（仓库层）
 * 只为[HttpRequestManager]服务
 * */
interface IHttpRequest {
    // TODO: 这里还要写返回值，到时候记得加上
    suspend fun getArchivesListCoroutine(@QueryMap map: Map<String,String>)
    : ArchiveData
}