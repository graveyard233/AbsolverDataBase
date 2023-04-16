package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.*
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo

@Dao
interface BilibiliVideoDAO {

    @Query("select * from bilibili_video")
    suspend fun getAllVideo() : List<BilibiliVideo>

    @Query("delete from bilibili_video")
    suspend fun deleteAll()

    @Upsert/*Insert(onConflict = OnConflictStrategy.IGNORE, entity = BilibiliVideo::class)*/
    suspend fun upsertAll(videos: List<BilibiliVideo>)
}