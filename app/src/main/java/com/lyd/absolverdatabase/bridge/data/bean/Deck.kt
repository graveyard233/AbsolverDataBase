package com.lyd.absolverdatabase.bridge.data.bean

data class Deck(

    var name :String,

    var deckType :DeckType,
    var createTime :Long,
    var updateTime :Long,
    var sequenceUpperRight :SequenceAttack
)

/**序列攻击*/
data class SequenceAttack(
    // 这里用站架朝向来区分序列攻击，因为序列攻击是定死的四个起始站架，所以用起始站架来区分序列类型
    val type :StandSide,

)
/**招式*/
data class Move(
    val name: String,// 名称
    val school :MoveSchool,// 流派
    val startSide: StandSide,// 起始站架
    val endSide: StandSide,// 结束站架
    val strength :Int,// 力度
    val attackToward :AttackToward,
    val attackAltitude: AttackAltitude,
    val attackDirection: AttackDirection,
    val startFrame :Float,// 起手帧数
    val physicalOutput :Float,// 体力消耗
    val advantageFrame :Float,// 优势帧
    val effect :MoveEffect,// 招式效果
)


