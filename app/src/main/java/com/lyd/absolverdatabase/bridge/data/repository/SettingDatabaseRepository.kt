package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.bean.MoveCE
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.repository.clients.RetrofitClient
import com.lyd.absolverdatabase.bridge.data.repository.database.JsonTXT
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveCEDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Jsoup

class SettingDatabaseRepository(private val moveOriginDAO: MoveOriginDAO,
                                private val moveCEDAO: MoveCEDAO) {
    private val TAG = javaClass.simpleName

    /**使用本地json同步原版招式数据表*/
    suspend fun syncMoveOriginTable(){
        moveOriginDAO.upsertAll(GsonUtils.fromJson(JsonTXT.moveOriginJson,GsonUtils.getListType(MoveOrigin::class.java)))
    }

    /**使用本地json同步CE招式数据表*/
    suspend fun syncMoveCETableFromLocal(){
        moveCEDAO.upsertAll(GsonUtils.fromJson(JsonTXT.moveCEJson,GsonUtils.getListType(MoveCE::class.java)))
    }

    suspend fun syncMoveCETableFromCloudFlow() : Flow<DataResult<Long>> {
        return flow<DataResult<Long>> {
            val startTime = TimeUtils.curTime
            val responseBody = RetrofitClient.csdnService.getCSDNHtml()
            val htmlStr = responseBody.string()
            val doc = Jsoup.parse(htmlStr)
            val jsonText = doc.getElementsByTag("article").first()
                ?.select("div.article_content")?.first()
                ?.getElementById("content_views")
                ?.getElementsByTag("pre")?.first()
                ?.select("code.prism")?.first()?.text()
//            Log.i(TAG, "syncMoveCETableFromCloudFlow: $jsonText")
            if (!jsonText.isNullOrEmpty()){
                val tempList = GsonUtils.fromJson<List<MoveCE>>(jsonText,GsonUtils.getListType(MoveCE::class.java))
//                tempList.takeLast(20).forEach {
//                    Log.i(TAG, "syncMoveCETableFromCloud: $it")
//                }
                moveCEDAO.upsertAll(tempList)
                val endTime = TimeUtils.curTime
                emit(DataResult.Success((endTime - startTime) / 1000))
            } else {
                emit(DataResult.Error("jsonText is null or empty"))
            }
        }.catch {
            emit(DataResult.Error(it.message!!))
        }.flowOn(Dispatchers.IO)
    }

}