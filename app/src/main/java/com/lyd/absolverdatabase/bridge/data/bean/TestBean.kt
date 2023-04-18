package com.lyd.absolverdatabase.bridge.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.lyd.absolverdatabase.utils.GsonUtils

// TODO: 进行多对多测试
@Entity(tableName = "test_a")
data class TestA(
    @PrimaryKey
    val aid :Int,
    val age :Int
)

@Entity(tableName = "test_b")
@TypeConverters(TestConverter::class,TestIntConverter::class)
data class TestB(
    @PrimaryKey
    val bid :Int,
    val listInt :MutableList<Int>,
//    val listString :MutableList<String>,
    val listTestSmall :MutableList<TestSmall>
)

data class TestSmall(
    val type :TestEnum,
    val uid :Int
)

enum class TestEnum{
    A,B
}

object TestDataGenerate{
    fun generateTestB():List<TestB>{
        val tempList = mutableListOf<TestB>()
        for (i in 0..10){
            tempList.add(TestB(i, getListInt(), getListTestSmall()))
        }
        return tempList
    }

    private fun getListInt():MutableList<Int>{
        val tempList = mutableListOf<Int>()
        for (i in 0..5){
            tempList.add((1..10).random())
        }
        return tempList
    }

    private fun getListTestSmall():MutableList<TestSmall>{
        val tempList = mutableListOf<TestSmall>()
        for (i in 0..5){
            tempList.add(TestSmall(uid = i, type = if (i % 2 == 0) TestEnum.A else TestEnum.B))
        }
        return tempList
    }
}

class TestConverter{

    @TypeConverter
    fun listSmallToJson(list: List<TestSmall>):String = GsonUtils.toJson(list)

    @TypeConverter
    fun jsonToListSmall(json: String):MutableList<TestSmall> = GsonUtils.fromJson(json,GsonUtils.getListType(TestSmall::class.java))
}

class TestIntConverter{// 注意这里的类型不能写成list，必须和原来的一模一样
    @TypeConverter
    fun listIntToJson(list: MutableList<Int>):String = GsonUtils.toJson(list)

    @TypeConverter
    fun jsonToListInt(json :String):MutableList<Int> = GsonUtils.fromJson(json,GsonUtils.getListType(Int::class.java))

}