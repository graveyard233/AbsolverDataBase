package com.lyd.absolverdatabase.bridge.data.repository.clients

import android.content.Context
import android.util.Log
import com.lyd.absolverdatabase.utils.MMKVUtils
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClient {

    val mClient : OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        buildClient()
    }

    private fun buildClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().apply {
            addInterceptor(logging)
//            followRedirects(true) // 允许失败重定向
//            cookieJar(MyCookieJar())
        }.build()
    }

    val cookieClient : OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder().apply {
            addInterceptor(logging)
            followRedirects(true)
            cookieJar(object : CookieJar {
                private val cookieStore : HashMap<String,List<Cookie>> = HashMap<String,List<Cookie>>()
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: ArrayList<Cookie>()
                }

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    Log.i("lyd", "$url -> saveFromResponse: $cookies")
                    MMKVUtils.put("mCookie",cookies)
                }
            })
        }.build()
    }
}

class MyCookieJar : CookieJar{
//    private val cookieStore : HashMap<String,List<Cookie>> = HashMap<String,List<Cookie>>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return MMKVUtils.getList("mCookie")
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // 这里不要保存，防止覆盖
    }
}
