package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveCN

@Dao
interface MoveCNDAO {
    @Query("delete from moveCN_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list :List<MoveCN>)

    @Query("select * from moveCN_tb")
    suspend fun getAllMoveCN() :List<MoveCN>

}