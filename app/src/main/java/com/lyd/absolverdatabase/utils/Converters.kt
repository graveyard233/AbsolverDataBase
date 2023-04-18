package com.lyd.absolverdatabase.utils

import androidx.room.TypeConverter
import com.lyd.absolverdatabase.bridge.data.bean.Move

class MoveListConverter{
    @TypeConverter
    fun moveListToJson(moveList: List<Move>) :String = GsonUtils.toJson(moveList)

    @TypeConverter
    fun jsonToMoveList(json :String) :List<Move> = GsonUtils.fromJson(json,GsonUtils.getListType(Move::class.java))
}