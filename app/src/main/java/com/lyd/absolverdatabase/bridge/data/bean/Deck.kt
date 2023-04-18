package com.lyd.absolverdatabase.bridge.data.bean

data class Deck(

    var name :String,

    var deckType :DeckType,
    var createTime :Long,
    var updateTime :Long,

    // 序列攻击
    var sequenceUpperRight :SequenceAttack,
    var sequenceLowerRight :SequenceAttack,
    var sequenceUpperLeft :SequenceAttack,
    var sequenceLowerLeft :SequenceAttack,
    // 自选攻击
    var optionalUpperRight :OptionalAttack,
    var optionalLowerRight :OptionalAttack,
    var optionalUpperLeft :OptionalAttack,
    var optionalLowerLeft :OptionalAttack
)
/**
 * 序列攻击这个类可以不需要，因为我可以通过deck的参数名来判断起始站架
 * 序列攻击也不应该持有list的实例，而是应该通过官方提供的多对多关系来实现连接，要么使用list<Move.id>来实现关系，
 * 但这又有一个问题，就是到时候查询的时候会非常麻烦，所以用前面那个方法
 * 自选攻击也可以通过deck的参数名来判断起始站架，所以应该也使用同上的方式来实现
 * */
// TODO: 需要更正实现方式，使用官方的多对多的方式来，得先去测试
/**序列攻击*/
data class SequenceAttack(
    // 这里用站架朝向来区分序列攻击，因为序列攻击是定死的四个起始站架，所以用起始站架来区分序列类型
    val type :StandSide,
    val moveList: MutableList<Move>// 招式列表
){
    fun endSide() : DeckResult<StandSide> {
        return if (moveList.isNotEmpty()){
            Success(moveList.last().endSide)
        } else if (moveList.isEmpty()){
            Empty("moveList is empty")
        } else {
            Error("未知error")
        }
    }

    fun moveCount() :Int = moveList.size
}

/**自选攻击*/
data class OptionalAttack(
    val type: StandSide,
    var move: Move
){
    fun endSide() :StandSide = move.endSide
}

/**招式*/
data class Move(
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


