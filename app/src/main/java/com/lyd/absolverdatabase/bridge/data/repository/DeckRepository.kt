package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveJsDAO
import com.lyd.absolverdatabase.utils.DeckGenerate
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.MoveGenerate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeckRepository(private val deckDao: DeckDAO,private val moveJsDao: MoveJsDAO) {

    private val TAG = javaClass.simpleName

    // TODO: 实现初始化时查询数据库并将json转成实体类，之后都靠这个查询
    private var moveList = listOf<Move>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            moveJsDao.deleteAll()
            deckDao.deleteAll()



            moveJsDao.upsertAll(MoveGenerate.generateMoveJsons())

            initMoveList()

            deckDao.upsertAll(DeckGenerate.generateDeck())

            Log.i(TAG, "decksJson -> ${GsonUtils.toJson(deckDao.getAllDeck())}")

            Log.i(TAG, "moveJson -> ${GsonUtils.toJson(moveList.dropLast(5))}")

            getMoveListByIdList(listOf(0,6,4,2)).forEachIndexed { index, move ->
                Log.i(TAG, "No.$index-> ${move}")
            }

        }
    }

    suspend fun queryDecksByDeckType(type :DeckType) :Flow<RepoResult<List<Deck>>> {
        return flow<RepoResult<List<Deck>>> {
            Log.i(TAG, "queryDecksByDeckType: 现在开始查询")
            val list = deckDao.getDecksByDeckType(type)
            if (list.isNotEmpty()){
                emit(RpSuccess(list))
            } else {
                emit(RpEmpty("list is empty"))
            }
        }.conflate()// 它的特性是，只接收处理最新的数据，如果有新数据到来了而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消。
            .catch {
                emit(RpError(it.message!!))
            }
            .flowOn(Dispatchers.IO)
    }


    /**根据Id列表获取招式列表，按idList顺序输出*/
    suspend fun getMoveListByIdList(idList: List<Int>):MutableList<Move>{
        if (moveList.isEmpty())
            initMoveList()
        val tempMap = idList.associateWith { it }
        val result = moveList.filter { it.id in tempMap }
        val finalResult = mutableListOf<Move>()
        // 筛出来之后重排
        idList.onEachIndexed { idIndex, id ->
            flag@for (move in result){
                if (move.id == id){
                    finalResult.add(idIndex,move)
                    break@flag
                }
            }
        }
        return finalResult
    }

    /**根据Id获取单个招式*/
    suspend fun getMoveById(id :Int):Move{
        if (moveList.isEmpty())
            initMoveList()
        return moveList[id]
    }

    /**起始站架筛选招式*/
    suspend fun getMoveListByStartSide(start :StandSide) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.startSide == start }
    }

    /**结束站架筛选招式*/
    suspend fun getMoveListByEndSide(end :StandSide) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.endSide == end }
    }

    /**攻击朝向筛选*/
    suspend fun getMoveListByAttackToward(toward: AttackToward) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.attackToward == toward }
    }

    /**攻击高度筛选*/
    suspend fun getMoveListByAttackAltitude(altitude: AttackAltitude) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.attackAltitude == altitude }
    }

    /**攻击走向筛选*/
    suspend fun getMoveListByAttackDirection(direction: AttackDirection) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.attackDirection == direction }
    }

    /**招式特效筛选*/
    suspend fun getMoveListByMoveEffect(effect: MoveEffect) :List<Move>{
        if (moveList.isEmpty())
            initMoveList()
        return moveList.filter { it.effect == effect }
    }

    /**初始化招式列表*/
    private suspend fun initMoveList(){
        // 重新在数据库里面拿一次数据并保存在DeckRepository里面
        moveList = moveJsDao.getAllMove().map {
            GsonUtils.fromJson<Move>(it.json,GsonUtils.getType(Move::class.java))
        }
    }

}