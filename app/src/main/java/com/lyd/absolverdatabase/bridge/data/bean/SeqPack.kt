package com.lyd.absolverdatabase.bridge.data.bean

data class SeqPack (
    val idList: MutableList<Int> = mutableListOf<Int>(-1,-1,-1),
    val originList: MutableList<MoveOrigin?> = mutableListOf<MoveOrigin?>(null,null,null)
){
    /**替换招式,会同时替换[idList]和[originList]*/
    fun replaceList(list: List<MoveOrigin?>){
        list.forEachIndexed { index, origin:MoveOrigin? ->
            if (origin != null){
                originList[index] = origin
                idList[index] = origin.id
            }
        }
    }
}