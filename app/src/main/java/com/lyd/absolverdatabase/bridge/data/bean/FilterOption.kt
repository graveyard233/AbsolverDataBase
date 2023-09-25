package com.lyd.absolverdatabase.bridge.data.bean

import androidx.core.text.isDigitsOnly
import java.util.concurrent.atomic.AtomicInteger

data class FilterOption(
    var attackToward: AttackTowardOption,
    var attackAltitude: AttackAltitudeOption,
    var attackDirection: AttackDirectionOption,
    var strengthList: MutableList<Boolean>,// 代表着力度筛选 012分别是轻中重
    var rangeRange :Int,// 攻击范围筛选 1.00-4.00 但目前最低的攻击范围为1.58 所以默认为 150400
    var effectSet: HashSet<String>,// 特效集合
    var startFrameRange :Int,// 起手帧数筛选 10-25 所以默认是 1025
    var phyWeaknessRange :Int,// 削体筛选 范围是0.0-180.0 所以默认是 1800
    var phyOutputRange :Int,// 耗体筛选 范围是10.0-30.0 但目前体力消耗最高是27.7 所以默认是 100285
    var hitAdvRange :Int,// 命中优势帧 可选范围是0-20 但目前招式范围在3-16 所以默认是 217
    var defAdvRange :String// 防御优势帧 范围是-2-20 但目前招式范围在-2-16 所以默认是 -2_17
) {

    companion object{
        const val defRange = 150400
        const val defStartF = 1025
        const val defPhyWeakness = 1800
        const val defPhyOutput = 100285
        const val defHitAdv = 217
        const val defDefAdv = "-2_17"
        fun list2RangeForRange(rangeList: List<Float>) :Int{
            return (rangeList[0] * 100).toInt() * 1000 + (rangeList[1] * 100).toInt()
        }
        fun range2ListForRange(rangeInt: Int) :List<Float>{
            return listOf(
                ((rangeInt - rangeInt % 1000) / 100000.00).toFloat(),
                ((rangeInt % 1000) / 100.00).toFloat()
            )
        }

        fun list2RangeForStartF(rangeList: List<Float>) :Int{
            return (rangeList[0].toInt() * 100) + rangeList[1].toInt()
        }
        fun range2ListForStartF(rangeInt: Int) :List<Float>{
            return listOf(
                (rangeInt / 100).toFloat(),
                (rangeInt % 100).toFloat()
            )
        }

        fun list2RangeForWeakness(rangeList: List<Float>) :Int{
            return ((rangeList[0] * 10).toInt() * 10000) + (rangeList[1] * 10).toInt()
        }
        fun range2ListForWeakness(rangeInt: Int) :List<Float>{
            return listOf(
                ((rangeInt / 10000) / 10.0).toFloat(),
                ((rangeInt % 10000) / 10.0).toFloat()
            )
        }

        fun list2RangeForOutput(rangeList: List<Float>) :Int{
            return ((rangeList[0] * 10).toInt() * 1000) + (rangeList[1] * 10).toInt()
        }
        fun range2ListForOutput(rangeInt: Int) :List<Float>{
            return listOf(
                ((rangeInt / 1000) / 10.0).toFloat(),
                ((rangeInt % 1000) / 10.0).toFloat()
            )
        }

        fun list2RangeForHitAdv(rangeList: List<Float>) :Int{
            return (rangeList[0].toInt() * 100) + rangeList[1].toInt()
        }
        fun range2ListForHitAdv(rangeInt: Int) :List<Float>{
            return listOf(
                (rangeInt / 100).toFloat(),
                (rangeInt % 100).toFloat()
            )
        }

        /**因为有负数的数据，所以只好用这个办法*/
        fun list2RangeForDefAdv(rangeList: List<Float>) :String{
            return "${rangeList[0].toInt()}_${rangeList[1].toInt()}"
        }
        /**
         * 这里曾出现[StringIndexOutOfBoundsException]的问题,是由split时[substring]超出范围导致的，所以这里暂时使用其他工具类来拆分文本
         * */
        fun range2ListForDefAdv(rangeStr :String) :List<Float>{
            val tempStrList = org.apache.commons.lang3.StringUtils.split(rangeStr,"_")
            var temp1 = "-2"
            var temp2 = "17"
            tempStrList.forEachIndexed { index, s ->
                if (s.isDigitsOnly()){
                    when(index){
                        0 -> temp1 = s
                        1 -> temp2 = s
                    }
                }
            }
            return listOf(
                temp1.toFloat(),
                temp2.toFloat()
            )
        }
    }
//    var effect :String = MoveEffect.NULL.toString()
//    var startFrame :Int ?= 0

    fun changeAll(tempFilter :FilterOption){
        attackToward = tempFilter.attackToward
        attackAltitude = tempFilter.attackAltitude
        attackDirection = tempFilter.attackDirection
        strengthList = mutableListOf(tempFilter.strengthList[0],tempFilter.strengthList[1],tempFilter.strengthList[2])
        rangeRange = tempFilter.rangeRange
        effectSet = hashSetOf<String>().apply { addAll(tempFilter.effectSet) }
        startFrameRange = tempFilter.startFrameRange
        phyWeaknessRange = tempFilter.phyWeaknessRange
        phyOutputRange = tempFilter.phyOutputRange
        hitAdvRange = tempFilter.hitAdvRange
        defAdvRange = tempFilter.defAdvRange
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

        if (rangeRange != otherFilter.rangeRange)
            tempFlag++

        if (effectSet != otherFilter.effectSet)
            tempFlag++

        if (startFrameRange != otherFilter.startFrameRange)
            tempFlag++

        if (phyWeaknessRange != otherFilter.phyWeaknessRange)
            tempFlag++

        if (phyOutputRange != otherFilter.phyOutputRange)
            tempFlag++

        if (hitAdvRange != otherFilter.hitAdvRange)
            tempFlag++

        if (defAdvRange != otherFilter.defAdvRange)
            tempFlag++

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
    class thrust :AttackDirectionOption(2,"戳击")
    class all :AttackDirectionOption(3,"全部走向")

    companion object{
        fun getRandomOption() :AttackDirectionOption{
            return when((0..3).random()){
                horizontal().num->horizontal()
                vertical().num->vertical()
                thrust().num->thrust()
                all().num->all()
                else -> { all() }
            }
        }
        private val optList by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            listOf(horizontal(),vertical(),thrust(),all())
        }
        fun getOptions() :List<AttackDirectionOption> = optList
        fun getOptionStr() :List<String> =
            listOf(horizontal().name,vertical().name,thrust().name,all().name)
    }
}
