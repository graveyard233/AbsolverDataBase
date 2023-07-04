package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.MoveCE
import com.lyd.absolverdatabase.bridge.data.bean.StandSide

@Dao
interface MoveCEDAO {
    @Query("delete from moveCE_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list :List<MoveCE>)

    @Query("select * from moveCE_tb")
    suspend fun getAllMoveCE() :List<MoveCE>

    @Query("select * from moveCE_tb where id = :id")
    suspend fun getMoveById(id :Int) :MoveCE

    @Query("select * from moveCE_tb where (startSide = :startSide or startSide = :mirrorStartSide) and canHand = 1")
    suspend fun getHandMoveByStartWithMirror(startSide: StandSide, mirrorStartSide: StandSide) :List<MoveCE>

    @Query("select * from moveCE_tb where (endSide = :endSide or endSide = :mirrorEndSide) and canHand = 1")
    suspend fun getHandMoveByEndWithMirror(endSide: StandSide,mirrorEndSide: StandSide) :List<MoveCE>

    /**在剑卡组中筛选镜像过于麻烦，还是交给仓库层中干，sql实在是不会写*/
    @Query("select * from moveCE_tb where canOriginSword = 1 or canMirrorSword = 1")
    suspend fun getSwordMove() :List<MoveCE>

}