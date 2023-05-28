package com.lyd.absolverdatabase.bridge.data.bean

sealed class SideLimit{
    data class noLimit(val msg: String = "nothing") :SideLimit()
    data class limitStart(val startSide: StandSide) :SideLimit()
    data class limitEnd(val endSide: StandSide) :SideLimit()
    data class limitAll(val startSide: StandSide,val endSide: StandSide) :SideLimit()
}
