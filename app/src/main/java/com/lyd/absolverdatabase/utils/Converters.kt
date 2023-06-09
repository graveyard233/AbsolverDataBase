package com.lyd.absolverdatabase.utils

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox

class IntMutableListConverter{
    private val type = object : TypeToken<MutableList<Int>>() {}.type
    @TypeConverter
    fun intMutableListToJson(intList: MutableList<Int>) :String = GsonUtils.toJson(intList)

    @TypeConverter
    fun jsonToIntMutableList(json: String) :MutableList<Int> = GsonUtils.fromJson(json,type)
}
class MoveBoxListConverter{
    private val type = object :TypeToken<MutableList<MoveBox>>() {}.type
    @TypeConverter
    fun moveBoxListToJson(boxList: MutableList<MoveBox>) :String = GsonUtils.toJson(boxList)
    @TypeConverter
    fun jsonToMoveBoxList(json :String) :MutableList<MoveBox> = GsonUtils.fromJson(json,type)
}
class MoveBoxConverter{
    @TypeConverter
    fun boxToJson(moveBox: MoveBox) :String = GsonUtils.toJson(moveBox)
    @TypeConverter
    fun jsonToBox(json: String) :MoveBox = GsonUtils.fromJson(json,MoveBox::class.java)
}