package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveCE

@Dao
interface MoveCEDAO {
    @Query("delete from moveCE_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list :List<MoveCE>)

    @Query("select * from moveCE_tb")
    suspend fun getAllMoveCE() :List<MoveCE>

}