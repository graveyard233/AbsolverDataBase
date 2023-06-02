package com.lyd.absolverdatabase.bridge.data.bean

sealed class SideLimit{
    data class noLimit(val msg: String = "nothing") :SideLimit()
    data class limitStart(val startSide: StandSide) :SideLimit()
    data class limitEnd(val endSide: StandSide) :SideLimit()
    data class limitAll(val startSide: StandSide,val endSide: StandSide) :SideLimit()
    /**注意，这是专门给自选序列用的限制，只有起始站架，但结束站架不能和起始站架相同，这也是一个限制*/
    data class optLimit(val startSide: StandSide) :SideLimit()
}
