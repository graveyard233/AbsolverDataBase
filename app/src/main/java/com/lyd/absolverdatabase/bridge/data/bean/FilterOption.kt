package com.lyd.absolverdatabase.bridge.data.bean

import java.util.concurrent.atomic.AtomicInteger

data class FilterOption(
    var attackToward: AttackTowardOption,
    var attackAltitude: AttackAltitudeOption,
    var attackDirection: AttackDirectionOption,
    var strengthList: MutableList<Boolean>// 代表着力度筛选
) {
//    var effect :String = MoveEffect.NULL.toString()
//    var startFrame :Int ?= 0
    var changeBy :AtomicInteger = AtomicInteger(0)

    fun changeAll(tempFilter :FilterOption){
        attackToward = tempFilter.attackToward
        attackAltitude = tempFilter.attackAltitude
        attackDirection = tempFilter.attackDirection
        strengthList = mutableListOf(tempFilter.strengthList[0],tempFilter.strengthList[1],tempFilter.strengthList[2])
    }

    fun isFilterSame(otherFilter :FilterOption) :Boolean{
        var tempFlag = 0
        if (!(this.attackToward.num == otherFilter.attackToward.num &&
            this.attackAltitude.num == otherFilter.attackAltitude.num &&
            this.attackDirection.num == otherFilter.attackDirection.num)){
            tempFlag++
        }

        this.strengthList.forEachIndexed { index, b ->
            if (otherFilter.strengthList[index] != b){
                tempFlag++
            }
        }

        return tempFlag == 0/*this.attackToward.num == otherFilter.attackToward.num &&
                this.attackAltitude.num == otherFilter.attackAltitude.num &&
                this.attackDirection.num == otherFilter.attackDirection.num*/
    }
}

// 采用密封类seal class，而不是枚举，是看到说kotlin的枚举会生成大量的其他东西，影响性能，所以用密封类来实现枚举效果

sealed class AttackTowardOption(val num: Int,val name :String) {
    class left : AttackTowardOption(0,name = "左")
    class right : AttackTowardOption(1,name = "右")
    class all : AttackTowardOption(2, name = "左右均可")

    companion object {
        fun getRandomOption(): AttackTowardOption {
            return when ((0..2).random()) {
                left().num -> left()
                right().num -> right()
                all().num -> all()
                else -> all()
            }
        }

        private val optList by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            listOf(left(),
                right(),
                all())
        }
        fun getOptions() :List<AttackTowardOption> = optList
        fun getOptionStr() :List<String> =
            listOf(left().name,
                right().name,
                all().name)
    }
}

sealed class AttackAltitudeOption(val num: Int,val name: String){
    class height :AttackAltitudeOption(0,"高位")
    class middle :AttackAltitudeOption(1,"中位")
    class low :AttackAltitudeOption(2,"低位")
    class all :AttackAltitudeOption(3,"全部高度")

    companion object{
        fun getRandomOption():AttackAltitudeOption{
            return when((0..3).random()){
                height().num-> height()
                middle().num->middle()
                low().num->low()
                all().num->all()
                else -> { all() }
            }
        }

        private val optList by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            listOf(height(),middle(),low(),all())
        }
        fun getOptions() :List<AttackAltitudeOption> = optList;
        fun getOptionStr() :List<String> =
            listOf(height().name,middle().name,low().name,all().name)
    }
}

sealed class AttackDirectionOption(val num: Int,val name: String){
    class horizontal :AttackDirectionOption(0,"横向")
    class vertical :AttackDirectionOption(1,"纵向")
    class poke :AttackDirectionOption(2,"戳击")
    class all :AttackDirectionOption(3,"全部走向")

    companion object{
        fun getRandomOption() :AttackDirectionOption{
            return when((0..3).random()){
                horizontal().num->horizontal()
                vertical().num->vertical()
                poke().num->poke()
                all().num->all()
                else -> { all() }
            }
        }
        private val optList by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            listOf(horizontal(),vertical(),poke(),all())
        }
        fun getOptions() :List<AttackDirectionOption> = optList
        fun getOptionStr() :List<String> =
            listOf(horizontal().name,vertical().name,poke().name,all().name)
    }
}
