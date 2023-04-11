package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface BilibiliVideoDAO {

    @Query("select * from bilibili_video")
    suspend fun getAllVideo() : List<BilibiliVideo>

    @Query("delete from bilibili_video")
    suspend fun deleteAll()
}