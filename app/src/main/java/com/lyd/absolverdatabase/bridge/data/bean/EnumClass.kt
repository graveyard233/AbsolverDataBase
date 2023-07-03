package com.lyd.absolverdatabase.bridge.data.bean

enum class AbsolverVersion{
    ORIGIN,GhostPro
}

/**卡组类型*/
enum class DeckType {
    HAND,
    GLOVE,
    SWORD
}
/**流派*/
// TODO: 完善流派类别
enum class Style {
    WINDFALL,// 落风
    FORSAKEN,// 遗忘
    KAHLT,// 卡尔特流
    STAGGER,// 醉拳
    FAEJIN,// 截拳道
    SIFU// 师父，白眉拳 Pak Mei
}
/**站架朝向*/
enum class StandSide {
    UPPER_RIGHT, LOWER_RIGHT,
    UPPER_LEFT, LOWER_LEFT
}
/**攻击朝向*/
enum class AttackToward {
    LEFT, RIGHT;
    companion object{
        fun getMirrorToward(toward: AttackToward):AttackToward{
            return when(toward){
                AttackToward.LEFT -> AttackToward.RIGHT
                AttackToward.RIGHT -> AttackToward.LEFT
            }
        }
    }
}
/**攻击高度*/
enum class AttackAltitude {
    HEIGHT, MIDDLE, LOW
}
/**攻击走向*/
enum class AttackDirection {
    HORIZONTAL, VERTICAL, POKE
}
enum class MoveEffect(val str: String) {
    STOP("停止"),// 停止
    DODGE_UP("上闪"),// 上闪
    DODGE_LOW("下闪"),// 下闪
    DODGE_SIDE("侧闪"),// 侧闪
    BREAK_DEFENCES("破防"),// 破防
    SUPER_ARMOR("霸体"),// 霸体
    BLOCK_COUNTER("格挡反击"),// 格挡反击
    DOUBLE_ATTACK("二段"),// 二段
    TRIPLE_ATTACK("三段"),// 三段，用于CE Mod
    MID_LINE("中线"),// 中线
    MENTAL_BLOW("精神打击"),// 精神打击
    NULL("无效果")// 无效果
}