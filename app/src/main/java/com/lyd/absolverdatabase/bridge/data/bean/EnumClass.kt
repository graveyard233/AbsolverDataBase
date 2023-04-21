package com.lyd.absolverdatabase.bridge.data.bean

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
    LEFT, RIGHT
}
/**攻击高度*/
enum class AttackAltitude {
    HEIGHT, MIDDLE, LOW
}
/**攻击走向*/
enum class AttackDirection {
    HORIZONTAL, VERTICAL, POKE
}
enum class MoveEffect {
    STOP,// 停止
    DODGE_UP,// 上闪
    DODGE_LOW,// 下闪
    DODGE_SIDE,// 侧闪
    BREAK_DEFENCES,// 破防
    SUPER_ARMOR,// 霸体
    NULL// 无效果
}