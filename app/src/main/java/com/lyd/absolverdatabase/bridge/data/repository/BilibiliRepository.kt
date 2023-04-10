package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.bean.VideoSearchData
import com.lyd.absolverdatabase.bridge.data.repository.clients.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BilibiliRepository {

    private val TAG :String = "${javaClass.simpleName}-${javaClass.hashCode()}"

    suspend fun getListFlow(map: MutableMap<String,String>) :Flow<DataResult<VideoSearchData<BilibiliVideo>>>
    {
        return flow {
            val response = RetrofitClient.service.getSearchList(map)
            when(response.code){
                0 ->{
                    emit(DataResult.Success(response.data))
                }
                else ->{
                    emit(DataResult.Error(response.message))
                }
            }
        }.catch {
            emit(DataResult.Error(it.message!!))
        }.flowOn(Dispatchers.IO)
    }



    suspend fun getBaseCookie(): Flow<String> {// 这里先获取一次cookie
        return flow {
            val body = RetrofitClient.cookieService.getCookie("https://bilibili.com/")
            emit("ok")
        }.catch {
            Log.e(TAG, "getRetrofitCookie: ${it.message}")
        }.flowOn(Dispatchers.IO)
    }


}