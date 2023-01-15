package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.ArchiveData

class HttpRequestManager :IHttpRequest{
    override suspend fun getArchivesListCoroutine(map: Map<String, String>): ArchiveData {
        TODO("Not yet implemented")
    }
}