package com.lyd.absolverdatabase.utils

import com.lyd.absolverdatabase.bridge.data.bean.*
import java.util.Date

object MoveGenerate{

    private val TAG = javaClass.simpleName

    private val ORIGIN = "原版"
    private val GP = "GP"

    fun generateMoveJsons():MutableList<MoveJson> {
        val tempList = mutableListOf<MoveJson>()
        val moveList = generateMoveOrigins()
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
    fun generateMoveOrigins() :MutableList<MoveOrigin>{
        val tempList = mutableListOf<MoveOrigin>()
        for( i in 0 .. 10){
            tempList.add(
                MoveOrigin(
                    id = i,
                    name = StringUtils.getRandomString((5..15).random()),
                    name_en = StringUtils.getRandomString((5..15).random()),
                    school = Style.values().random(),
                    startSide = StandSide.values().random(),
                    endSide = StandSide.values().random(),
                    strength = (1..3).random(),
                    attackRange = ((75..300).random() / 10).toFloat(),
                    attackToward = AttackToward.values().random(),
                    attackAltitude = AttackAltitude.values().random(),
                    attackDirection = AttackDirection.values().random(),
                    startFrame = (i + 5..i + 20).random(),
                    physicalWeakness =  ((30..150).random() / 10).toFloat(),
                    physicalOutput = ((30..150).random() / 10).toFloat(),
                    hitAdvantageFrame = (i..20).random(),
                    defenseAdvantageFrame = (i..15).random(),
                    effect = MoveEffect.values().random(),
                    canHand = i % 3 == 0,
                    canSword = i % 4 == 0
                )
            )
        }
        return tempList
    }

    fun generateMoveGPs() :MutableList<MoveGP>{
        val tempList = mutableListOf<MoveGP>()
        for( i in 0 .. 10){
            tempList.add(
                MoveGP(
                    id = i,
                    name = StringUtils.getRandomString((5..15).random()),
                    name_en = StringUtils.getRandomString((5..15).random()),
                    school = Style.values().random(),
                    startSide = StandSide.values().random(),
                    endSide = StandSide.values().random(),
                    strength = (1..3).random(),
                    attackRange = ((75..300).random() / 10).toFloat(),
                    attackToward = AttackToward.values().random(),
                    attackAltitude = AttackAltitude.values().random(),
                    attackDirection = AttackDirection.values().random(),
                    startFrame = (i + 5..i + 20).random(),
                    physicalWeakness =  ((30..150).random() / 10).toFloat(),
                    physicalOutput = ((30..150).random() / 10).toFloat(),
                    hitAdvantageFrame = (i..20).random(),
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
                    name = StringUtils.getRandomString((5..15).random()),
                    deckType = DeckType.values().random(),
                    deckStyle = Style.values().random(),
//                    if (i % 2 == 0) DeckType.HAND else DeckType.SWORD,
                    createTime = Date().time,
                    updateTime = Date().time + (0..10000).random(),
                    note = StringUtils.getRandomString((10..60).random()),
                    sequenceUpperRight = mutableListOf((0..i).random(),(0..i).random()),
                    sequenceLowerRight = mutableListOf((0..i).random(),(0..i).random()),
                    sequenceUpperLeft = mutableListOf((0..i).random(),(0..i).random()),
                    sequenceLowerLeft = mutableListOf((0..i).random(),(0..i).random()),
                    optionalUpperRight = (0..i).random(),
                    optionalLowerRight = (0..i).random(),
                    optionalUpperLeft = (0..i).random(),
                    optionalLowerLeft = (0..i).random()
                )
            )
        }
        return tempList
    }
}