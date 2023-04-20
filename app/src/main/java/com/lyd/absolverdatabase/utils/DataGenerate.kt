package com.lyd.absolverdatabase.utils

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import java.util.Date

object MoveGenerate{

    private val TAG = javaClass.simpleName

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
    fun generateMoves() :MutableList<Move>{
        val tempList = mutableListOf<Move>()
        for( i in 0 .. 10){
            tempList.add(
                Move(
                    name = StringUtils.getRandomString((5..15).random()),
                    name_en = StringUtils.getRandomString((5..15).random()),
                    school = Style.values().random(),
                    startSide = StandSide.values().random(),
                    endSide = StandSide.values().random(),
                    strength = (20..100).random(),
                    attackToward = AttackToward.values().random(),
                    attackAltitude = AttackAltitude.values().random(),
                    attackDirection = AttackDirection.values().random(),
                    startFrame = (i+5..i+20).random(),
                    physicalWeakness = (10..50).random(),
                    physicalOutput = (10..50).random(),
                    hitAdvantageFrame = (i..15).random(),
                    defenseAdvantageFrame = (i..15).random(),
                    effect = MoveEffect.values().random(),
                    canHand = i % 3 == 0,
                    canSword = i % 4 == 0
                )
            )
        }

        return tempList
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