package com.lyd.absolverdatabase.bridge.data.bean

import android.os.Parcelable
import androidx.room.Ignore
import kotlinx.parcelize.Parcelize

/**
 * @param isUseMirror 1是使用镜像，0是没用镜像
 * */
@Parcelize
data class MoveBox(
    var moveId :Int = -1,
    var isUseMirror :Int = 0,
) : Parcelable{
    companion object{
        fun getIdList(moveList: List<MoveBox>) :List<Int>{
            return moveList.slice(0..2).map {
                it.moveId
            }.toMutableList()
//            val tempList = mutableListOf<Int>()
//            for (i in 0..2){
//                tempList.add(moveList[i].moveId)
//            }
//            return tempList
        }
        fun getMirrorList(moveList: List<MoveBox>) :MutableList<Int>{
            return  moveList.slice(0..2).map {
                it.isUseMirror
            }.toMutableList()
//            val tempList = mutableListOf<Int>(0,0,0)
//            for (i in 0..2){
//                tempList[i] = moveList[i].isUseMirror
//            }
//            return tempList
        }
    }
    /**招式，内部的数据和是否启用镜像是同步的，即假如[isUseMirror]是1，则[moveOrigin]内部已经做出了镜像转换了，不需要再变更*/
    @Ignore
    var moveOrigin :MoveOrigin ?= null
}
