package com.lyd.absolverdatabase.utils

import androidx.annotation.DrawableRes
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.StandSide

object SideUtil {
    val UPPER_RIGHT = 0
    val UPPER_LEFT = 1
    val LOWER_LEFT = 2
    val LOWER_RIGHT = 3

    @DrawableRes
    fun imgId(sideInt: Int):Int{
        return when(sideInt){
            UPPER_RIGHT -> R.drawable.ic_upper_right
            UPPER_LEFT ->R.drawable.ic_upper_left
            LOWER_LEFT ->R.drawable.ic_lower_left
            LOWER_RIGHT ->R.drawable.ic_lower_right
            else ->R.drawable.ic_upper_right
        }
    }
    @DrawableRes
    fun imgIdBySide(side: StandSide):Int{
        return when(side){
            StandSide.UPPER_RIGHT -> R.drawable.ic_upper_right
            StandSide.UPPER_LEFT ->R.drawable.ic_upper_left
            StandSide.LOWER_LEFT ->R.drawable.ic_lower_left
            StandSide.LOWER_RIGHT ->R.drawable.ic_lower_right
            else ->R.drawable.ic_upper_right
        }
    }
}