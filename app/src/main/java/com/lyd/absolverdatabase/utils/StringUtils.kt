package com.lyd.absolverdatabase.utils

import android.util.Base64
import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.DeckType
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.bridge.data.bean.Style
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.InflaterOutputStream
import kotlin.math.abs
import kotlin.math.log10

object StringUtils {

    private val azList :List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val sb = StringBuilder()

    fun getRandomString(size :Int = 10) :String
    = sb.apply {
        clear()
        (0..size).onEach { append(azList.random()) }
    }.toString()

//    private val idTagS = "<id>"
//    private val idTagE = "<_id>"
    private const val naTagS = "<na>"
    private const val naTagE = "<_na>"
    private const val tyTagS = "<ty>"
    private const val tyTagE = "<_ty>"
    private const val stTagS = "<st>"
    private const val stTagE = "<_st>"
    private const val ctTagS = "<ct>"
    private const val ctTagE = "<_ct>"
    private const val utTagS = "<ut>"
    private const val utTagE = "<_ut>"
    private const val noTagS = "<no>"
    private const val noTagE = "<_no>"

    /**将卡组按照我的方法压缩成文本*/
    fun deck2MyJson(deck :Deck,moreDetail :String = "") :String{
        sb.apply {
            clear()
//            append("$idTagS${deck.deckId}$idTagE")// 因为id总要变成-2，所以这里没有必要写上卡组id
            if (SettingRepository.isShowSeqDetailWhenSharedDeck && moreDetail.isNotEmpty()){// 如果要显示招式名称
                append(moreDetail)
            }

            append("$naTagS${deck.name}$naTagE")
            append("$tyTagS${deck.deckType}$tyTagE")
            append("$stTagS${deck.deckStyle}$stTagE")
            append("$ctTagS${deck.createTime}$ctTagE")
            append("$utTagS${deck.updateTime}$utTagE")
            append("$noTagS${deck.note}$noTagE")
            deck.sequenceUpperRight.forEach { box ->
                when(box.moveId){
                    -1 -> append("999${box.isUseMirror}")
                    else ->{
                        when(box.moveId.length()){
                            1 -> append("00${box.moveId}${box.isUseMirror}")
                            2 -> append("0${box.moveId}${box.isUseMirror}")
                            3 -> append("${box.moveId}${box.isUseMirror}")
                        }
                    }
                }
            }
            deck.sequenceUpperLeft.forEach { box ->
                when(box.moveId){
                    -1 -> append("999${box.isUseMirror}")
                    else ->{
                        when(box.moveId.length()){
                            1 -> append("00${box.moveId}${box.isUseMirror}")
                            2 -> append("0${box.moveId}${box.isUseMirror}")
                            3 -> append("${box.moveId}${box.isUseMirror}")
                        }
                    }
                }
            }
            deck.sequenceLowerLeft.forEach { box ->
                when(box.moveId){
                    -1 -> append("999${box.isUseMirror}")
                    else ->{
                        when(box.moveId.length()){
                            1 -> append("00${box.moveId}${box.isUseMirror}")
                            2 -> append("0${box.moveId}${box.isUseMirror}")
                            3 -> append("${box.moveId}${box.isUseMirror}")
                        }
                    }
                }
            }
            deck.sequenceLowerRight.forEach { box ->
                when(box.moveId){
                    -1 -> append("999${box.isUseMirror}")
                    else ->{
                        when(box.moveId.length()){
                            1 -> append("00${box.moveId}${box.isUseMirror}")
                            2 -> append("0${box.moveId}${box.isUseMirror}")
                            3 -> append("${box.moveId}${box.isUseMirror}")
                        }
                    }
                }
            }
            when(deck.optionalUpperRight.moveId){
                -1 -> append("999${deck.optionalUpperRight.isUseMirror}")
                else ->{
                    when(deck.optionalUpperRight.moveId.length()){
                        1 -> append("00${deck.optionalUpperRight.moveId}${deck.optionalUpperRight.isUseMirror}")
                        2 -> append("0${deck.optionalUpperRight.moveId}${deck.optionalUpperRight.isUseMirror}")
                        3 -> append("${deck.optionalUpperRight.moveId}${deck.optionalUpperRight.isUseMirror}")
                    }
                }
            }
            when(deck.optionalUpperLeft.moveId){
                -1 -> append("999${deck.optionalUpperLeft.isUseMirror}")
                else ->{
                    when(deck.optionalUpperLeft.moveId.length()){
                        1 -> append("00${deck.optionalUpperLeft.moveId}${deck.optionalUpperLeft.isUseMirror}")
                        2 -> append("0${deck.optionalUpperLeft.moveId}${deck.optionalUpperLeft.isUseMirror}")
                        3 -> append("${deck.optionalUpperLeft.moveId}${deck.optionalUpperLeft.isUseMirror}")
                    }
                }
            }
            when(deck.optionalLowerLeft.moveId){
                -1 -> append("999${deck.optionalLowerLeft.isUseMirror}")
                else ->{
                    when(deck.optionalLowerLeft.moveId.length()){
                        1 -> append("00${deck.optionalLowerLeft.moveId}${deck.optionalLowerLeft.isUseMirror}")
                        2 -> append("0${deck.optionalLowerLeft.moveId}${deck.optionalLowerLeft.isUseMirror}")
                        3 -> append("${deck.optionalLowerLeft.moveId}${deck.optionalLowerLeft.isUseMirror}")
                    }
                }
            }
            when(deck.optionalLowerRight.moveId){
                -1 -> append("999${deck.optionalLowerRight.isUseMirror}")
                else ->{
                    when(deck.optionalLowerRight.moveId.length()){
                        1 -> append("00${deck.optionalLowerRight.moveId}${deck.optionalLowerRight.isUseMirror}")
                        2 -> append("0${deck.optionalLowerRight.moveId}${deck.optionalLowerRight.isUseMirror}")
                        3 -> append("${deck.optionalLowerRight.moveId}${deck.optionalLowerRight.isUseMirror}")
                    }
                }
            }
        }

        return sb.toString()
    }

    /**将文本解压成卡组*/
    fun myJson2Deck(myJson :String) :Deck{
        val sTagLength = 4
        val eTagLength = 5

        val id = -2

        val naStartIndex = myJson.indexOf(naTagS)
        val naEndIndex = myJson.indexOf(naTagE)
        val name = myJson.substring(naStartIndex + sTagLength,naEndIndex)

        val tyStartIndex = naEndIndex + eTagLength
        val tyEndIndex = myJson.indexOf(tyTagE)
        val type = myJson.substring(tyStartIndex + sTagLength,tyEndIndex)

        val stStartIndex = tyEndIndex + eTagLength
        val stEndIndex = myJson.indexOf(stTagE)
        val style = myJson.substring(stStartIndex + sTagLength,stEndIndex)

        val ctStartIndex = stEndIndex + eTagLength
        val ctEndIndex = myJson.indexOf(ctTagE)
        val createTime = myJson.substring(ctStartIndex + sTagLength,ctEndIndex)

        val utStartIndex = ctEndIndex + eTagLength
        val utEndIndex = myJson.indexOf(utTagE)
        val updateTime = myJson.substring(utStartIndex + sTagLength,utEndIndex)

        val noStartIndex = utEndIndex + eTagLength
        val noEndIndex = myJson.indexOf(noTagE)
        val note = myJson.substring(noStartIndex + sTagLength,noEndIndex)

        val moveMsg = myJson.takeLast(64)// 因为是定长，所以可以直接取
        val msgSb = StringBuilder(moveMsg)

        val seqList = mutableListOf<MutableList<MoveBox>>(
            mutableListOf(MoveBox(), MoveBox(),MoveBox()),
            mutableListOf(MoveBox(), MoveBox(),MoveBox()),
            mutableListOf(MoveBox(), MoveBox(),MoveBox()),
            mutableListOf(MoveBox(), MoveBox(),MoveBox())
        )
        val optList = mutableListOf<MoveBox>(MoveBox(),MoveBox(),MoveBox(),MoveBox())
        for (i in msgSb.indices step 12){
            if (i < 48){
                val tempSeq = msgSb.substring(i,i+12)// 这就是序列攻击数据
//                Log.i(TAG, "myJson2Deck: $i $temp")
                for (j in tempSeq.indices step 4){
                    val oneBoxStr = tempSeq.substring(j,j+4)// 这是一个招式的信息
                    if (oneBoxStr.take(3) == "999"){
                        continue
                    }
                    seqList[i/12][j/4] = MoveBox(moveId = oneBoxStr.take(3).toInt(), isUseMirror = oneBoxStr.takeLast(1).toInt())
                }
            } else {
                val tempOpt = msgSb.substring(i,i+16)// 这里长为16
                for (j in tempOpt.indices step 4){
                    val oneBoxStr = tempOpt.substring(j,j+4)// 这是一个招式的信息
                    if (oneBoxStr.take(3) == "999"){
                        continue
                    }
                    optList[j/4] = MoveBox(moveId = oneBoxStr.take(3).toInt(), isUseMirror = oneBoxStr.takeLast(1).toInt())
                }
                break // 因为剩下的就是opt，一次性处理完，不用再做了
            }
        }

        return Deck(
            deckId = id,
            name = name,
            deckType = when (type) {
                "HAND" -> DeckType.HAND
                "GLOVE" -> DeckType.GLOVE
                "SWORD" -> DeckType.SWORD
                else -> DeckType.HAND
            },
            deckStyle = when (style) {
                "WINDFALL" -> Style.WINDFALL
                "FORSAKEN" -> Style.FORSAKEN
                "KAHLT" -> Style.KAHLT
                "STAGGER" -> Style.STAGGER
                "FAEJIN" -> Style.FAEJIN
                "SIFU" ->Style.SIFU
                else -> Style.WINDFALL
            },
            createTime = createTime.toLong(),
            updateTime = updateTime.toLong(),
            note = note,
            sequenceUpperRight = seqList[0],
            sequenceUpperLeft = seqList[1],
            sequenceLowerLeft = seqList[2],
            sequenceLowerRight = seqList[3],
            optionalUpperRight = optList[0],
            optionalUpperLeft = optList[1],
            optionalLowerLeft = optList[2],
            optionalLowerRight = optList[3]
        )
    }


    private fun Int.length() = when(this){
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

}