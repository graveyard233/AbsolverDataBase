package com.lyd.absolverdatabase.bridge.data.bean

data class OptPack(
    var optionA :Int = -1,
    var optionMove :MoveOrigin? = null
){
    /**会同时更新[optionA]和[optionMove]*/
    fun updateOpt(move :MoveOrigin?){
        if (move != null){
            optionA = move.id
            optionMove = move
        }
    }
}
