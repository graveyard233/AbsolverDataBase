package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.bean.VideoSearchData
import com.lyd.absolverdatabase.bridge.data.repository.clients.RetrofitClient
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.BilibiliVideoDAO
import com.lyd.absolverdatabase.utils.MMKVUtils
import com.lyd.architecture.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Cookie

class BilibiliRepository(private val videoDAO: BilibiliVideoDAO) {

    private val TAG :String = "${javaClass.simpleName}-${javaClass.hashCode()}"

    suspend fun getListFlow(map: MutableMap<String,String>,isManualRefresh :Boolean = false) :Flow<DataResult<List<BilibiliVideo>>>
    {
        return flow {
            when(isManualRefresh){
                true ->{// 是手动刷新，就进行一次网络请求
                    val response = RetrofitClient.service.getSearchList(map).apply {
                        data.result.forEach {
                            if (it.title.contains("<em class=\"keyword\">")){// 要移除keyword的注释
                                it.title = it.title.replace("<em class=\"keyword\">","").replace("</em>","")
                            }
                        }
                        videoDAO.insertAll(data.result)
                        // TODO: 先看看是不是手动刷新，不是手动刷新就从数据库拿视频数据，否则再进行网络请求
                        // TODO: 一般来说，这里需要把网络请求到的数据传入本地数据库
                    }
                    when(response.code){
                        0 ->{
                            emit(DataResult.Success(response.data.result))
                        }
                        else ->{
                            emit(DataResult.Error(response.message))
                        }
                    }
                }
                false ->{// 不是手动刷新就从本地数据库发射数据
                    emit(DataResult.Success(videoDAO.getAllVideo()))
                }
            }


        }.catch {
            emit(DataResult.Error(it.message!!))
        }.flowOn(Dispatchers.IO)
    }



    suspend fun getBaseCookie(): Flow<String> {// 这里先获取一次cookie
        return flow {
            MMKVUtils.getList<Cookie>("mCookie").apply {
                if (isEmpty()){
                    Log.i(TAG, "getBaseCookie: 网络请求获取cookie")
                    RetrofitClient.cookieService.getCookie("https://www.bilibili.com/")
                    emit(this.toString())
                } else {
                    Log.i(TAG, "getBaseCookie: 读取本地cookie")
                    val result = (this[1].expiresAt -  TimeUtils.curTime) / (1000*3600*24) // 转换为天数
                    if (result < 60){// 只要cookie的时间少于60天，也就是过了300天，我就要更新一次cookie
                        RetrofitClient.cookieService.getCookie("https://www.bilibili.com/")
                    }
                    emit("${this[1].expiresAt} - ${TimeUtils.curTime} = $result")
                }
            }

        }.catch {
            Log.e(TAG, "getRetrofitCookie: ${it.message}")
        }.flowOn(Dispatchers.IO)
    }


}