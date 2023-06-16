package com.lyd.absolverdatabase.bridge.data.bean

data class OptPack(
    var optionA :Int = -1,
    var optionMove :MoveOrigin? = null,
    var isMirror :Int = 0,
    val startSide: StandSide
){
    /**会同时更新[optionA],[optionMove]和[isMirror]*/
    fun updateOpt(move :MoveOrigin?){
        if (move != null){
            optionA = move.id
            optionMove = move
            isMirror = if (move.startSide != startSide) 1 else 0
        }
    }
}
