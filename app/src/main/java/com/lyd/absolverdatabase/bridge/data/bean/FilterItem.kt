package com.lyd.absolverdatabase.bridge.data.bean

data class FilterItem(
    val tag :String,
    var isChecked :Boolean = true,
    var name :String
){
    companion object{
        const val STRENGTH = "strength"
        const val RANGE = "range"
        const val EFFECT = "effect"
        const val START_FRAME = "startFrame"
        const val PHYSICAL_WEAKNESS = "physicalWeakness"
        const val PHYSICAL_OUTPUT = "physicalOutput"
        const val HIT_ADVANTAGE_FRAME = "hitAdvantageFrame"
        const val DEF_ADVANTAGE_FRAME = "defenseAdvantageFrame"
    }
}