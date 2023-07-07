package com.lyd.absolverdatabase.bridge.data.repository.api

import okhttp3.ResponseBody
import retrofit2.http.*

interface ICSDNApi {
    companion object{
        const val BASE_URL = "https://blog.csdn.net/"
    }

    @Streaming
    @Headers("User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
        "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    @GET("graveyard233/article/details/131581015")
    suspend fun getCSDNHtml() :ResponseBody
}