package com.lyd.absolverdatabase.utils

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.tencent.mmkv.MMKV

object MMKVUtils {
    /**
     * mmkv 存储对象
     */
    fun put(key: String, obj: Any) {
        MMKV.defaultMMKV().encode(key, Gson().toJson(obj))
    }

    /**
     * mmkv 取出对象
     */
    inline fun <reified T> get(key: String): T? {
        val json = MMKV.defaultMMKV().decodeString(key)
        return Gson().fromJson(json, T::class.java)
    }

    /**
     * mmkv 取出List对象
     */
    inline fun <reified T> getList(key: String): List<T> {
        val json = MMKV.defaultMMKV().decodeString(key)
        return getObjectList(json, T::class.java)
    }


    fun <T> getObjectList(jsonString: String?, cls: Class<T>?): List<T> {
        val list: MutableList<T> = ArrayList()
        try {
            val gson = Gson()
            val arry = JsonParser.parseString(jsonString).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }


}
