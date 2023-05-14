package com.lyd.absolverdatabase.bridge.data.bean

data class FilterOption(
    var attackToward: AttackTowardOption,
//    var attackAltitude: AttackAltitude,
//    var attackDirection: AttackDirection
) {
//    var effect :String = MoveEffect.NULL.toString()
//    var startFrame :Int ?= 0
}


sealed class AttackTowardOption(val num: Int) {
    class left : AttackTowardOption(1)
    class right : AttackTowardOption(2)
    class all : AttackTowardOption(3)

    companion object {
        fun getRandomOption(): AttackTowardOption {
            return when ((0..2).random()) {
                0 -> left()
                1 -> right()
                2 -> all()
                else -> all()
            }
        }
    }
}
