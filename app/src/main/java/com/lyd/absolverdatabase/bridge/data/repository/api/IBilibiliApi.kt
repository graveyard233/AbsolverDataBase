package com.lyd.absolverdatabase.bridge.data.repository.api

import com.lyd.absolverdatabase.bridge.data.bean.ArchiveData
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliMsg
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.bean.VideoSearchData
import okhttp3.ResponseBody
import retrofit2.http.*

interface IBilibiliApi {
//    https://api.bilibili.com/x/polymer/space/seasons_archives_list?mid=11736402&season_id=23870&sort_reverse=false&page_num=1&page_size=30
    companion object{
        const val BASE_URL = "https://api.bilibili.com/x/" // 注意，baseUrl末尾不带斜杠
    }

    // 使用协程来主力开发
    @GET("polymer/space/seasons_archives_list")
    suspend fun getArchivesList(@QueryMap map: Map<String,String>)
    : BilibiliMsg<ArchiveData> // 这玩意是返回值




    @GET("web-interface/search/type")
    suspend fun getSearchList(@QueryMap map: Map<String,String>)
            : BilibiliMsg<VideoSearchData<BilibiliVideo>>


    @Streaming
    @GET
    suspend fun getCookie(@Url url: String)
            : ResponseBody
}