package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.Move

@Dao
interface MoveDAO {

    @Query("delete from move_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<Move>)

    @Query("select * from move_tb")
    suspend fun getAllMove() :List<Move>
}