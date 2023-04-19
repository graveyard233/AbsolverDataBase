package com.lyd.absolverdatabase.bridge.data.bean

import androidx.room.*
import com.google.gson.reflect.TypeToken
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
    val listTestSmall :MutableList<TestSmall>
)

// 首先，为您的两个实体分别创建一个类。
// 多对多关系与其他关系类型均不同的一点在于，子实体中通常不存在对父实体的引用。
// 因此，需要创建第三个类来表示两个实体之间的关联实体（即交叉引用表）。
// 交叉引用表中必须包含表中表示的多对多关系中每个实体的主键列。

// add这个实例就能实现
@Entity(tableName = "a_with_b",primaryKeys = ["aid","bid"])
data class TestABCrossRef(
    val aid: Int,
    val bid: Int,
    val attackType: String,
    val startSide :String
)

data class TestAWithBs(
    @Embedded
    val testA: TestA,
    @Relation(
        parentColumn = "aid",
        entityColumn = "bid",
        associateBy = Junction(TestABCrossRef::class)
    )
    val listB :List<TestB>
)

data class TestSmall(
    val type :TestEnum,
    val uid :Int
)

enum class TestEnum{
    A,B
}

object TestDataGenerate{

    fun generateTestA():List<TestA>{
        val tempList = mutableListOf<TestA>()
        for (i in 0..5)
            tempList.add(TestA(i,(0..100).random()))
        return tempList
    }


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
    fun jsonToListInt(json :String):MutableList<Int> {
        val type = object :TypeToken<MutableList<Int>>() {}.type
        return GsonUtils.fromJson(json,type/*GsonUtils.getListType(Int::class.java)*/)
    }

}