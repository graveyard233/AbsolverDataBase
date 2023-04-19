package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.TestA
import com.lyd.absolverdatabase.bridge.data.bean.TestABCrossRef
import com.lyd.absolverdatabase.bridge.data.bean.TestAWithBs
import com.lyd.absolverdatabase.bridge.data.bean.TestB

@Dao
interface TestDAO {


    @Query("delete from test_a")
    suspend fun deleteAllA()

    @Upsert
    suspend fun upsertAllA(list: List<TestA>)

    @Query("select * from test_b")
    suspend fun getAllTest() : List<TestB>

    @Query("delete from test_b")
    suspend fun deleteAllB()

    @Query("delete from test_b where bid = :bid")
    suspend fun deleteOneB(bid :Int)

    @Upsert
    suspend fun upsertAllB(list: List<TestB>)

    @Query("delete from a_with_b")
    suspend fun deleteAWithB()

    @Upsert
    suspend fun upsetAAndB(list: List<TestABCrossRef>)

    @Transaction
    @Query("select * from test_a")
    suspend fun getAWithBs() :List<TestAWithBs>


}