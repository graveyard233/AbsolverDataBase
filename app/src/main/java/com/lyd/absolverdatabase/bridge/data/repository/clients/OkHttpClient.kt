package com.lyd.absolverdatabase.bridge.data.repository.clients

import android.util.Log
import com.lyd.absolverdatabase.utils.MMKVUtils
import okhttp3.*
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
            cookieJar(MyCookieJar())
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
            addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                    .newBuilder()
                    .removeHeader("User-Agent")// 移除默认的UA，使用桌面浏览器的UA
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
                    .build()

                 chain.proceed(request)
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
