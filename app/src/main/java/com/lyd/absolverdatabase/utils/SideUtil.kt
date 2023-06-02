package com.lyd.absolverdatabase.utils

import androidx.annotation.DrawableRes
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.StandSide
import com.lyd.absolverdatabase.bridge.data.bean.StandSide.*

object SideUtil {
    val UPPER_RIGHT = 0
    val UPPER_LEFT = 1
    val LOWER_LEFT = 2
    val LOWER_RIGHT = 3

    @DrawableRes
    fun imgIdForOneMove(sideInt: Int):Int{
        return when(sideInt){
            UPPER_RIGHT -> R.drawable.ic_upper_right
            UPPER_LEFT ->R.drawable.ic_upper_left
            LOWER_LEFT ->R.drawable.ic_lower_left
            LOWER_RIGHT ->R.drawable.ic_lower_right
            else ->R.drawable.ic_upper_right
        }
    }
    @DrawableRes
    fun imgIdForMoves(side: StandSide):Int{
        return when(side){
            StandSide.UPPER_RIGHT -> R.drawable.ic_upper_right_bold
            StandSide.UPPER_LEFT ->R.drawable.ic_upper_left_bold
            StandSide.LOWER_LEFT ->R.drawable.ic_lower_left_bold
            StandSide.LOWER_RIGHT ->R.drawable.ic_lower_right_bold
        }
    }

    fun getSideByInt(@androidx.annotation.IntRange(0,3) sideInt: Int):StandSide {
        return when(sideInt){
            UPPER_RIGHT -> StandSide.UPPER_RIGHT
            UPPER_LEFT -> StandSide.UPPER_LEFT
            LOWER_LEFT -> StandSide.LOWER_LEFT
            LOWER_RIGHT -> StandSide.LOWER_RIGHT
            else -> StandSide.UPPER_RIGHT
        }
    }
    fun getIntBySide(side: StandSide):Int {
        return when(side){
            StandSide.UPPER_RIGHT -> UPPER_RIGHT
            StandSide.LOWER_RIGHT -> LOWER_RIGHT
            StandSide.UPPER_LEFT -> UPPER_LEFT
            StandSide.LOWER_LEFT -> LOWER_LEFT
        }
    }

    fun getMirrorSide(@androidx.annotation.IntRange(0,3) sideInt: Int) :StandSide{
        return when(sideInt){
            UPPER_RIGHT -> StandSide.UPPER_LEFT
            UPPER_LEFT -> StandSide.UPPER_RIGHT
            LOWER_LEFT -> StandSide.LOWER_RIGHT
            LOWER_RIGHT -> StandSide.LOWER_LEFT
            else -> StandSide.UPPER_LEFT
        }
    }
    fun getMirrorInt(side: StandSide):Int{
        return when(side){
            StandSide.UPPER_RIGHT -> UPPER_LEFT
            StandSide.LOWER_RIGHT -> LOWER_LEFT
            StandSide.UPPER_LEFT -> UPPER_RIGHT
            StandSide.LOWER_LEFT -> LOWER_RIGHT
        }
    }
}