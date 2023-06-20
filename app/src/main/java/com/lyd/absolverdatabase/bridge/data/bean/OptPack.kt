package com.lyd.absolverdatabase.bridge.data.bean

data class OptPack(
    var optionA :Int = -1,
    var optionMove :MoveOrigin? = null,
    var isMirror :Int = 0,
    val startSide: StandSide
){
    /**会同时更新[optionA],[optionMove]和[isMirror]*/
    fun updateOpt(moveOrigin :MoveOrigin?,isUseMirror :Int = 0){
        optionA = moveOrigin?.id ?: -1
        if (isUseMirror != 0){
            moveOrigin?.toMirror()
        }
        optionMove = moveOrigin
        isMirror = isUseMirror
    }

    fun updateOptByMoveForSelect(moveForSelect: MoveForSelect?){
        optionA = moveForSelect?.moveOrigin?.id ?: -1
        optionMove = moveForSelect?.moveOrigin
        isMirror = moveForSelect?.isMirror ?: 0
    }
}
