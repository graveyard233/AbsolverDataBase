package com.lyd.absolverdatabase.bridge.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lyd.absolverdatabase.utils.IntMutableListConverter

@Entity(tableName = "deck_tb")
@TypeConverters(IntMutableListConverter::class)
data class Deck(

    @PrimaryKey
    var name :String,

    var deckType :DeckType,
    var createTime :Long,
    var updateTime :Long,

    // 这里拿的是Move的id，以这种list来确定关系，不直接持有move实例来防止后期move出问题了这边也要改
    // 要用的时候靠id去move_tb那搜索就行
    // 序列攻击
    var sequenceUpperRight :MutableList<Int>,
    var sequenceLowerRight :MutableList<Int>,
    var sequenceUpperLeft :MutableList<Int>,
    var sequenceLowerLeft :MutableList<Int>,
    // 自选攻击
    var optionalUpperRight :Int,
    var optionalLowerRight :Int,
    var optionalUpperLeft :Int,
    var optionalLowerLeft :Int
)

/**招式*/
@Entity(tableName = "move_tb")
data class Move(
    @PrimaryKey
    val id :Int,
    val name: String,// 名称
    val school :Style,// 流派
    val startSide: StandSide,// 起始站架
    val endSide: StandSide,// 结束站架
    val strength :Int,// 力度
    val attackToward :AttackToward,// 攻击朝向
    val attackAltitude: AttackAltitude,// 攻击高度
    val attackDirection: AttackDirection,// 攻击走向
    val startFrame :Float,// 起手帧数
    val physicalOutput :Float,// 体力消耗
    val advantageFrame :Float,// 优势帧
    val effect :MoveEffect,// 招式效果
)


