package com.lyd.absolverdatabase.bridge.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lyd.absolverdatabase.utils.IntMutableListConverter
import com.lyd.absolverdatabase.utils.MoveBoxConverter
import com.lyd.absolverdatabase.utils.MoveBoxListConverter
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "deck_tb")
@TypeConverters(value = [
    IntMutableListConverter::class,
    MoveBoxListConverter::class,
    MoveBoxConverter::class
])
data class Deck(

    @PrimaryKey
    var name :String,

    var deckType :DeckType,
    var deckStyle: Style,
    var createTime :Long,
    var updateTime :Long,

    var note :String = "",// 卡组说明

    // 这里拿的是Move的id，以这种list来确定关系，不直接持有move实例来防止后期move出问题了这边也要改
    // 要用的时候靠id去move_tb那搜索就行
    // 序列攻击
    var sequenceUpperRight :MutableList<MoveBox>,
    var sequenceLowerRight :MutableList<MoveBox>,
    var sequenceUpperLeft :MutableList<MoveBox>,
    var sequenceLowerLeft :MutableList<MoveBox>,
    // 自选攻击
    var optionalUpperRight :MoveBox,
    var optionalLowerRight :MoveBox,
    var optionalUpperLeft :MoveBox,
    var optionalLowerLeft :MoveBox
) : Parcelable

/**专门存数据库的招式类*/
@Entity(tableName = "moveJs_tb")
data class MoveJson(
    @PrimaryKey
    val id: Int,
    val json :String// 这个专门存move的json文件，防止未来字段更新，而维护更新太难了
)


// TODO: 使用两个类来区分原版和GP版，我可以放心的使用数据库存两个表了，马上就把上面的带json的给删了
/**
 * 招式
 * 和GP版有修改的可以使用ArrayMap来存储版本
 * */
@Entity(tableName = "moveOrigin_tb")
data class MoveOrigin(
    @PrimaryKey
    val id :Int,// id，保持和MoveJson的id一致，用于检索
    val name: String,// 名称
    val name_en:String,// 英文名称
    val school :Style,// 流派
    var startSide: StandSide,// 起始站架 可变是为了镜像修改
    var endSide: StandSide,// 结束站架 可变是为了镜像修改
    val strength :Int,// 力度 1~3，分别为轻中重
    val attackRange :Float,// 攻击范围，这个GP有修改
    var attackToward :AttackToward,// 攻击朝向 可变是为了镜像修改
    val attackAltitude: AttackAltitude,// 攻击高度
    val attackDirection: AttackDirection,// 攻击走向
    val startFrame :Int,// 起手帧数，这个GP有修改
    val physicalWeakness :Float,// 削弱对手体力，这个GP有修改
    val physicalOutput :Float,// 自身体力消耗，这个GP有修改
    val hitAdvantageFrame :Int,// 击中优势帧，这个GP有修改
    val defenseAdvantageFrame :Int,// 防御优势帧，这个GP有修改
    val effect :String,// 招式效果，用于整合多个招式效果，但注意这个只是string，不是枚举
    val canHand :Boolean,// 徒手是否可用
    val canOriginSword :Boolean,// 原版招式在剑卡组里是否可用
    val canMirrorSword :Boolean,// 镜像招式在剑卡组里是否可用 因为有些招式在剑卡组里面不能使用镜像搜索（起始结束站架被动画限死），所以加这个字段，两个字段其中有一个是1就可以在剑卡组中使用，全0就不能在剑卡组中所以用
){
    fun toMirror(){
        startSide = SideUtil.getMirrorSide(startSide)
        endSide = SideUtil.getMirrorSide(endSide)
        attackToward = AttackToward.getMirrorToward(attackToward)
    }
}

@Entity(tableName = "moveGP_tb")
data class MoveGP(
    @PrimaryKey
    val id :Int,// id，保持和MoveJson的id一致，用于检索
    val name: String,// 名称
    val name_en:String,// 英文名称
    val school :Style,// 流派
    val startSide: StandSide,// 起始站架
    val endSide: StandSide,// 结束站架
    val strength :Int,// 力度 1~3，分别为轻中重
    val attackRange :Float,// 攻击范围，这个GP有修改
    val attackToward :AttackToward,// 攻击朝向
    val attackAltitude: AttackAltitude,// 攻击高度
    val attackDirection: AttackDirection,// 攻击走向
    val startFrame :Int,// 起手帧数，这个GP有修改
    val physicalWeakness :Float,// 削弱对手体力，这个GP有修改
    val physicalOutput :Float,// 自身体力消耗，这个GP有修改
    val hitAdvantageFrame :Int,// 击中优势帧，这个GP有修改
    val defenseAdvantageFrame :Int,// 防御优势帧，这个GP有修改
    val effect :String,// 招式效果，用于整合多个招式效果，但注意这个只是string，不是枚举
    val canHand :Boolean,// 徒手是否可用
    val canOriginSword :Boolean,// 原版招式在剑卡组里是否可用
    val canMirrorSword :Boolean,// 镜像招式在剑卡组里是否可用 因为有些招式在剑卡组里面不能使用镜像搜索（起始结束站架被动画限死），所以加这个字段，两个字段其中有一个是1就可以在剑卡组中使用，全0就不能在剑卡组中所以用
)


