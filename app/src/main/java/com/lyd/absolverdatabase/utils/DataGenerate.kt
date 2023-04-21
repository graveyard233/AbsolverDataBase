package com.lyd.absolverdatabase.utils

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.lyd.absolverdatabase.bridge.data.bean.*
import java.util.Date

object MoveGenerate{

    private val TAG = javaClass.simpleName

    private val ORIGIN = "原版"
    private val GP = "GP"

    fun generateMoveJsons():MutableList<MoveJson> {
        val tempList = mutableListOf<MoveJson>()
        val moveList = generateMoves()
        (0..10).onEachIndexed { index, i ->
            tempList.add(
                MoveJson(
                    i,
                    GsonUtils.toJson(moveList[i])
                )
            )
        }
        return tempList
    }
    private fun generateMoves() :MutableList<Move>{
        val tempList = mutableListOf<Move>()
        for( i in 0 .. 10){
            tempList.add(
                Move(
                    id = i,
                    name = StringUtils.getRandomString((5..15).random()),
                    name_en = StringUtils.getRandomString((5..15).random()),
                    school = Style.values().random(),
                    startSide = StandSide.values().random(),
                    endSide = StandSide.values().random(),
                    strength = (1..3).random(),
                    attackRange = getFloatMapForMoveData(start = 75, end = 300),
                    attackToward = AttackToward.values().random(),
                    attackAltitude = AttackAltitude.values().random(),
                    attackDirection = AttackDirection.values().random(),
                    startFrame = getIntMapForMoveData(start = i+5, end = i + 20),
                    physicalWeakness =  getFloatMapForMoveData(),
                    physicalOutput =  getFloatMapForMoveData(),
                    hitAdvantageFrame = getIntMapForMoveData(start = i, end = 50),
                    defenseAdvantageFrame = getIntMapForMoveData(start = i, end = 15),
                    effect = MoveEffect.values().random(),
                    canHand = i % 3 == 0,
                    canSword = i % 4 == 0
                )
            )
        }
        return tempList
    }

    private fun getIntMapForMoveData(start :Int = 0, end :Int = 20) : ArrayMap<String,Int>{
        return arrayMapOf<String,Int>(
            ORIGIN to (start..end).random(),
            GP to (start..end - 5).random()
        )
    }

    private fun getFloatMapForMoveData(start :Int = 30, end :Int = 150) : ArrayMap<String,Float>{
        return arrayMapOf<String,Float>(
            ORIGIN to ((start..end).random() / 10).toFloat() ,
            GP to ((start..end - 5).random() / 10).toFloat()
        )
    }
}


object DeckGenerate{
    private val TAG = javaClass.simpleName

    fun generateDeck(count :Int = 5) :List<Deck>{
        val tempList = mutableListOf<Deck>()
        (0 until count).onEachIndexed { _, i ->
            tempList.add(
                Deck(
                    StringUtils.getRandomString((i+5..i+10).random()),
                    DeckType.values().random(),
//                    if (i % 2 == 0) DeckType.HAND else DeckType.SWORD,
                    Date().time,
                    Date().time + (0..10000).random(),
                    mutableListOf((0..i).random(),(0..i).random()),
                    mutableListOf((0..i).random(),(0..i).random()),
                    mutableListOf((0..i).random(),(0..i).random()),
                    mutableListOf((0..i).random(),(0..i).random()),
                    (0..i).random(),
                    (0..i).random(),
                    (0..i).random(),
                    (0..i).random()
                )
            )
        }
        return tempList
    }
}