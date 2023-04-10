package com.lyd.absolverdatabase.bridge.data.repository.clients

import com.lyd.absolverdatabase.bridge.data.repository.api.IBilibiliApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    @JvmStatic
    val service : IBilibiliApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Retrofit.Builder().apply {
            baseUrl(IBilibiliApi.BASE_URL)
            client(OkHttpClient.mClient)
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(IBilibiliApi::class.java)
    }

    val cookieService :IBilibiliApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Retrofit.Builder().apply {
            baseUrl(IBilibiliApi.BASE_URL)
            client(OkHttpClient.cookieClient)
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(IBilibiliApi::class.java)
    }
}