package com.lyd.absolverdatabase.bridge.data.bean

data class SeqPack (
    val idList: MutableList<Int> = mutableListOf<Int>(-1,-1,-1),
    val originList: MutableList<MoveOrigin?> = mutableListOf<MoveOrigin?>(null,null,null),
    val startSide: StandSide,
    val isMirrorList: MutableList<Int> = mutableListOf(0,0,0)
){
    /**替换招式,会同时替换[idList]和[originList]*/
    fun replaceList(list: List<MoveOrigin?>){
        list.forEachIndexed { index, origin:MoveOrigin? ->
            if (isMirrorList[index] > 0){// 假如是镜像使用，则把招式数据镜像化
                origin?.toMirror()
            }
            originList[index] = origin
            idList[index] = origin?.id ?: -1
        }
    }

    fun updateOne(whatIndex :Int,moveForSelect: MoveForSelect?){// 注意，moveForSelect是已经经过镜像修改的数据
        if (moveForSelect == null){
            idList[whatIndex] = -1
            originList[whatIndex] = null
            isMirrorList[whatIndex] = 0
        } else {
            if (whatIndex in 0..2){
                idList[whatIndex] = moveForSelect.moveOrigin.id
                originList[whatIndex] = moveForSelect.moveOrigin
                isMirrorList[whatIndex] = moveForSelect.isMirror
            }
        }
    }
}