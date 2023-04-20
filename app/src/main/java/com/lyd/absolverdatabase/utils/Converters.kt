package com.lyd.absolverdatabase.utils

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken

class IntMutableListConverter{
    private val type = object : TypeToken<MutableList<Int>>() {}.type
    @TypeConverter
    fun intMutableListToJson(intList: MutableList<Int>) :String = GsonUtils.toJson(intList)

    @TypeConverter
    fun jsonToIntMutableList(json: String) :MutableList<Int> = GsonUtils.fromJson(json,type)
}