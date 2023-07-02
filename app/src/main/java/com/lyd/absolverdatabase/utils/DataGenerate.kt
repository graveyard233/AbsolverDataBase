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
                    effect = MoveEffectGenerate.generateString(),
                    canHand = i % 3 == 0,
                    canOriginSword = i % 4 == 0,
                    canMirrorSword = i % 6 == 0
                )
            )
        }
        return tempList
    }

    fun generateMoveCNs() :MutableList<MoveCN>{
        val tempList = mutableListOf<MoveCN>()
        for( i in 0 .. 10){
            tempList.add(
                MoveCN(
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
                    cancelPoint = (1..15).random(),
                    hitFrame = listOf("4","3","5","1","2","2+2+2","3+2+2").random(),
                    yellowAttackFrame = (0..5).random(),
                    effect = MoveEffectGenerate.generateString(),
                    canHand = i % 3 == 0,
                    canOriginSword = i % 4 == 0,
                    canMirrorSword = i % 6 == 1
                )
            )
        }
        return tempList
    }

    /**生成空招式，用于占位。假如发现id为-1，则这个招式用空招式替换*/
    fun generateEmptyOriginMove():MoveOrigin = MoveOrigin(
        id = -1,
        name = "",
        name_en = "",
        school = Style.WINDFALL,
        startSide = StandSide.UPPER_RIGHT,
        endSide = StandSide.UPPER_RIGHT,
        strength = 0,
        attackRange = 0F,
        attackToward = AttackToward.RIGHT,
        attackAltitude = AttackAltitude.HEIGHT,
        attackDirection = AttackDirection.POKE,
        startFrame = 0,
        physicalWeakness = 0F,
        physicalOutput = 0F,
        hitAdvantageFrame = 0,
        defenseAdvantageFrame = 0,
        effect = MoveEffect.NULL.name,
        canHand = false,
        canOriginSword = false,
        canMirrorSword = false
    )
}


object DeckGenerate{
    private val TAG = javaClass.simpleName

    /**
     * 序列全为-1，size为3；自选攻击全为-1
     * */
    fun generateEmptyDeck(isFromDeckToEdit :Boolean = false,deckType :DeckType = DeckType.HAND):Deck = Deck(
        name = "",
        deckType = deckType,
        deckStyle = Style.WINDFALL,
        updateTime = if (isFromDeckToEdit) 1L else 0L,
        createTime = 0L,
        note = "",
        sequenceUpperRight = mutableListOf(MoveBox(), MoveBox(),MoveBox()),
        sequenceLowerRight = mutableListOf(MoveBox(), MoveBox(),MoveBox()),
        sequenceUpperLeft = mutableListOf(MoveBox(), MoveBox(),MoveBox()),
        sequenceLowerLeft = mutableListOf(MoveBox(), MoveBox(),MoveBox()),
        optionalUpperRight = MoveBox(),
        optionalLowerRight = MoveBox(),
        optionalUpperLeft = MoveBox(),
        optionalLowerLeft = MoveBox()
    )
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
                    sequenceUpperRight = mutableListOf(MoveBox((0..i).random()),MoveBox((0..i).random()),MoveBox((0..i).random())),
                    sequenceLowerRight = mutableListOf(MoveBox((0..i).random()),MoveBox((0..i).random()),MoveBox((0..i).random())),
                    sequenceUpperLeft = mutableListOf(MoveBox((0..i).random()),MoveBox((0..i).random()),MoveBox((0..i).random())),
                    sequenceLowerLeft = mutableListOf(MoveBox((0..i).random()),MoveBox((0..i).random()),MoveBox((0..i).random())),
                    optionalUpperRight = MoveBox((0..i).random()),
                    optionalLowerRight = MoveBox((0..i).random()),
                    optionalUpperLeft = MoveBox((0..i).random()),
                    optionalLowerLeft = MoveBox((0..i).random()),
                )
            )
        }
        return tempList
    }
}

object MoveEffectGenerate{
    private val TAG = javaClass.simpleName
    private val sb = StringBuilder()

    private fun generateList():List<MoveEffect>{
        val tempList = mutableListOf<MoveEffect>()
        for (i in 0..(0..1).random()){
            tempList.add(MoveEffect.values().random())
        }
        return tempList
    }


    fun generateString():String{
        sb.clear()
        val tempList = generateList()
        tempList.forEachIndexed { index, moveEffect ->
            if (index != tempList.size - 1){
                sb.append("$moveEffect,")
            } else{
                sb.append(moveEffect)
            }
        }
        return sb.toString()
    }
}