package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveJson
import com.lyd.absolverdatabase.bridge.data.repository.DeckRepository

@Dao
interface MoveJsDAO {

    @Query("delete from moveJs_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<MoveJson>)

    @Query("select * from moveJs_tb") /**只允许在 [DeckRepository.initMoveList] 中使用*/
    suspend fun getAllMove() :List<MoveJson>
}