package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.Archive
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.repository.clients.RetrofitClient
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.BilibiliVideoDAO
import com.lyd.absolverdatabase.utils.MMKVUtils
import com.lyd.absolverdatabase.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Cookie

class BilibiliRepository(private val videoDAO: BilibiliVideoDAO) {

    private val TAG :String = "${javaClass.simpleName}-${javaClass.hashCode()}"

    private val mRegex = "(赦免者)|(Absolver)".toRegex() // 有可能会搜出奇奇怪怪的视频，所以要靠tag来过滤


    // TODO: 这里还能把数据塞进本地数据库，就不用每次都网络请求了，但我觉得以后假如要加其他人的教学视频，可以一起塞进同一个表，然后按名称筛选
    suspend fun getLearnFlow(map: Map<String,String>) :Flow<DataResult<List<Archive>>>{
        return flow<DataResult<List<Archive>>> {

            val response = RetrofitClient.service.getArchivesList(map)
            when(response.code){
                0 -> {
                    emit(DataResult.Success(response.data.archives))
                }
                else ->{
                    emit(DataResult.Error(response.message))
                }
            }

        }.catch {
            emit(DataResult.Error(it.message!!))
        }.flowOn(Dispatchers.IO)

    }

    suspend fun getListFlow(map: MutableMap<String,String>,isManualRefresh :Boolean = false) :Flow<DataResult<List<BilibiliVideo>>>
    {
        return flow {
            when(isManualRefresh){
                true ->{// 是手动刷新，就进行一次网络请求
                    // 先看看是不是手动刷新，不是手动刷新就从数据库拿视频数据，否则再进行网络请求
                    // 一般来说，这里需要把网络请求到的数据传入本地数据库
                    val response = RetrofitClient.service.getSearchList(map).apply {
                        data.result = data.result.filter { it.tag.contains(mRegex) }
                        data.result.forEach {
                            if (it.title.contains("<em class=\"keyword\">")){// 要移除keyword的注释
                                it.title = it.title.replace("<em class=\"keyword\">","").replace("</em>","")
                            }
                            if (it.title.contains("&amp;")){
                                it.title = it.title.replace("&amp;","&")
                            }
                        }
                        videoDAO.upsertAll(data.result)

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



    suspend fun getBaseCookie() :List<Cookie>{
        MMKVUtils.getList<Cookie>("mCookie").apply {
            if (isEmpty()){
                Log.i(TAG, "getCookie: 网络请求获取cookie")
                RetrofitClient.cookieService.getCookie("https://www.bilibili.com/")
                return MMKVUtils.getList("mCookie")
            } else {
                Log.i(TAG, "getBaseCookie: 读取本地cookie")
                val result = (this[1].expiresAt -  TimeUtils.curTime) / (1000*3600*24) // 转换为天数
                if (result < 60){// 只要cookie的时间少于60天，也就是过了300天，我就要更新一次cookie
                    RetrofitClient.cookieService.getCookie("https://www.bilibili.com/")
                }
                return MMKVUtils.getList("mCookie")
            }
        }
    }


}