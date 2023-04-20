package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveJson

@Dao
interface MoveJsDAO {

    @Query("delete from moveJs_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<MoveJson>)

    @Query("select * from moveJs_tb")
    suspend fun getAllMove() :List<MoveJson>
}