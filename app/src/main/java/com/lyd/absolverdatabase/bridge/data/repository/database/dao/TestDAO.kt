package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.TestB

@Dao
interface TestDAO {

    @Query("select * from test_b")
    suspend fun getAllTest() : List<TestB>

    @Query("delete from test_b")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list: List<TestB>)

}