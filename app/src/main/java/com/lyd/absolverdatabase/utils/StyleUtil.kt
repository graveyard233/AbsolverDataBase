package com.lyd.absolverdatabase.utils

import androidx.annotation.DrawableRes
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Style

object StyleUtil {

    @DrawableRes
    fun styleId(style: Style):Int{
        return when(style){
            Style.WINDFALL -> R.drawable.ic_windfall
            Style.FORSAKEN -> R.drawable.ic_forsaken
            Style.KAHLT -> R.drawable.ic_kahlt
            Style.STAGGER -> R.drawable.ic_stagger
            Style.FAEJIN -> R.drawable.ic_faejin
            Style.SIFU -> R.drawable.sifu
        }
    }

}