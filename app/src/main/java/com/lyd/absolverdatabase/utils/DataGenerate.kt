package com.lyd.absolverdatabase.utils

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import java.util.Date

object MoveGenerate{

    private val TAG = javaClass.simpleName

    fun generateMoves() :MutableList<Move>{
        val tempList = mutableListOf<Move>()
        for( i in 0 .. 10){
            tempList.add(
                Move(i,
                    StringUtils.getRandomString((5..15).random()),
                    Style.values().random(),
                    StandSide.values().random(),
                    StandSide.values().random(),
//                    if (i % 2 == 0) Style.WINDFALL else Style.FORSAKEN,
//                    if (i % 2 == 0) StandSide.LOWER_LEFT else StandSide.UPPER_RIGHT,
//                    if (i % 3 == 0) StandSide.LOWER_RIGHT else StandSide.UPPER_LEFT,
                    (20..100).random(),
                    AttackToward.values().random(),
                    AttackAltitude.values().random(),
                    AttackDirection.values().random(),
//                    if (i % 3 == 1) AttackToward.LEFT else AttackToward.RIGHT,
//                    if (i % 3 == 2) AttackAltitude.HEIGHT else AttackAltitude.LOW,
//                    if (i % 2 == 0) AttackDirection.HORIZONTAL else AttackDirection.VERTICAL,
                    (i+5..i+20).random().toFloat(),
                    (10..50).random().toFloat(),
                    (i..15).random().toFloat(),
                    MoveEffect.values().random()
//                    if (i % 4 == 1) MoveEffect.BREAK_DEFENCES else MoveEffect.NULL
                )
            )
        }

        return tempList
    }
}


object DeckGenerate{
    private val TAG = javaClass.simpleName

    fun generateDeck() :List<Deck>{
        val tempList = mutableListOf<Deck>()
        (0 until 20).onEachIndexed { _, i ->
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