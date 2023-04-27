package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveGP

@Dao
interface MoveGPDAO {
    @Query("delete from moveGP_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list :List<MoveGP>)

    @Query("select * from moveGP_tb")
    suspend fun getAllMoveOrigin() :List<MoveGP>

}