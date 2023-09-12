package com.lyd.absolverdatabase.bridge.data.bean


data class OptPack(
    var optionA :Int = -1,
    var move :Move ?= null,
    var isMirror :Int = 0,
    val startSide: StandSide
){
    fun updateOpt(move: Move?,isUseMirror: Int = 0){
        optionA = move?.id ?: -1
        if (isUseMirror != 0){
            move?.toMirror()
        }
        this.move = move
        isMirror = isUseMirror
    }

    fun updateOptByMoveForSelect(moveForSelect: MoveForSelect?){
        optionA = moveForSelect?.move?.id ?: -1
        move = moveForSelect?.move
        isMirror = moveForSelect?.isMirror ?: 0
    }
}
