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
/**专门存数据库的招式类*/
@Entity(tableName = "moveJs_tb")
data class MoveJson(
    @PrimaryKey
    val id: Int,
    val json :String// 这个专门存move的json文件，防止未来字段更新，而维护更新太难了
)

/**招式*/
data class Move(
    val name: String,// 名称
    val name_en:String,// 英文名称
    val school :Style,// 流派
    val startSide: StandSide,// 起始站架
    val endSide: StandSide,// 结束站架
    val strength :Int,// 力度
    val attackToward :AttackToward,// 攻击朝向
    val attackAltitude: AttackAltitude,// 攻击高度
    val attackDirection: AttackDirection,// 攻击走向
    val startFrame :Int,// 起手帧数
    val physicalWeakness :Int,// 削弱对手体力
    val physicalOutput :Int,// 自身体力消耗
    val hitAdvantageFrame :Int,// 击中优势帧
    val defenseAdvantageFrame :Int,// 防御优势帧
    val effect :MoveEffect,// 招式效果
    val canHand :Boolean,// 徒手是否可用
    val canSword :Boolean// 剑卡是否可用
)


