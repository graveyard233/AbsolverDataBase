package com.lyd.absolverdatabase.bridge.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lyd.absolverdatabase.bridge.data.bean.*

@Dao
interface MoveOriginDAO {

    @Query("delete from moveOrigin_tb")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(list :List<MoveOrigin>)

    @Query("select * from moveOrigin_tb")
    suspend fun allMoves() :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where id = :id")
    suspend fun getMoveById(id :Int) :MoveOrigin

    @Query("select * from moveOrigin_tb where id in (:ids)")
    suspend fun getMovesByIds(ids :List<Int>) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where startSide = :startSide")
    suspend fun getMovesByStartSide(startSide: StandSide) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where endSide = :endSide")
    suspend fun getMovesByEndSide(endSide: StandSide) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where attackToward = :attackToward")
    suspend fun getMovesByAttackToward(attackToward: AttackToward) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where attackAltitude = :attackAltitude")
    suspend fun getMovesByAttackAltitude(attackAltitude: AttackAltitude) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where attackDirection = :attackDirection")
    suspend fun getMovesByAttackDirection(attackDirection: AttackDirection) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where effect = :effects")
    suspend fun getMovesByEffect(effects: List<MoveEffect>) :List<MoveOrigin>

    /**
     * 查询的这种join可以获取到map，然后根据map的各种的操作符来检索move，eg keys 和 values
     * 依靠join的条件id相等，注意，这里的id相等是一对一的关系，假如是一对多要设置多的哪一方为value，且返回的结果为list
     * 然后从这里集合中检索数据
     * */
    @Query("select * from moveOrigin_tb " +
            "join moveGP_tb on moveOrigin_tb.id = moveGP_tb.id " +
            "where moveOrigin_tb.id in (:ids) ")
    suspend fun getMoveMapByIds(ids: List<Int>) :Map<MoveOrigin,MoveGP>

    @Query("select * from moveOrigin_tb where endSide = :endSide and canHand = 1")// 注意，true在sql中是1和0
    suspend fun getHandMoveByEndSide(endSide: StandSide) :List<MoveOrigin>

    @Deprecated(message = "结束站架不可靠", replaceWith = ReplaceWith("getSwordMove()"))
    @Query("select * from moveOrigin_tb where endSide = :endSide and (canOriginSword = 1 or canMirrorSword = 1)")
    suspend fun getSwordMoveByEndSide(endSide: StandSide) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where startSide = :startSide and endSide = :endSide and canHand = 1")
    suspend fun getHandMoveBySide(startSide: StandSide, endSide: StandSide) :List<MoveOrigin>

    @Query("select * from moveOrigin_tb where (startSide = :startSide or startSide = :mirrorStartSide) and canHand = 1")
    suspend fun getHandMoveByStartWithMirror(startSide: StandSide, mirrorStartSide: StandSide) :List<MoveOrigin>

    /**在剑卡组中筛选镜像过于麻烦，还是交给仓库层中干，sql实在是不会写*/
    @Query("select * from moveOrigin_tb where canOriginSword = 1 or canMirrorSword = 1")
    suspend fun getSwordMove() :List<MoveOrigin>
}