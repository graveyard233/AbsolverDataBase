package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveJsDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.DeckGenerate
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.MoveGenerate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeckRepository(private val deckDao: DeckDAO,private val moveJsDao: MoveJsDAO,
                     private val moveOriginDAO: MoveOriginDAO,private val moveGPDAO: MoveGPDAO)
{

    private val TAG = javaClass.simpleName

    init {
        CoroutineScope(Dispatchers.IO).launch {
            moveJsDao.deleteAll()
            deckDao.deleteAll()
            moveOriginDAO.deleteAll()
            moveGPDAO.deleteAll()


            moveJsDao.upsertAll(MoveGenerate.generateMoveJsons())
            val r1 = async {
                moveOriginDAO.upsertAll(MoveGenerate.generateMoveOrigins())
            }
            val r2 = async {
                moveGPDAO.upsertAll(MoveGenerate.generateMoveGPs())
            }
            val r3 = async {
                deckDao.upsertAll(DeckGenerate.generateDeck(15))
            }
            r1.await()
            r2.await()
            r3.await()


            Log.i(TAG, "decksJson -> ${GsonUtils.toJson(deckDao.getAllDeck())}")


//            Log.i(TAG, "moveJson -> ${GsonUtils.toJson(moveOriginDAO.allMoves().dropLast(5))}")

            getOriginListByIdList(listOf(0,6,4,2)).forEachIndexed { index, move ->
                Log.i(TAG, "No.$index-> ${move}")
            }

//            moveOriginDAO.getMovesByStartSide(StandSide.values().random())
//                .forEach { move->
//                    Log.i(TAG, "move: ${move}")
//                }
            try {
                val tempMap = moveOriginDAO.getMoveMapByIds(listOf(1,3,0))
                tempMap.forEach { origin, gp ->
                    Log.i(TAG, "map: $origin <-> $gp")
                }
                tempMap.keys
            } catch (e: Exception) {
                Log.e(TAG, "error: ", e)
            }

        }
    }

    /*-------------------------------这里写卡组操作方法------------------------------------*/

    suspend fun queryDecksByDeckType(type :DeckType) :Flow<RepoResult<List<Deck>>> {
        return flow<RepoResult<List<Deck>>> {
            Log.i(TAG, "queryDecksByDeckType: 现在开始查询")
            val list = deckDao.getDecksByDeckType(type)
            if (list.isNotEmpty()){
                emit(RepoResult.RpSuccess(list))
            } else {
                emit(RepoResult.RpEmpty("list is empty"))
            }
        }.conflate()// 它的特性是，只接收处理最新的数据，如果有新数据到来了而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消。
            .catch {
                emit(RepoResult.RpError(it.message!!))
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun deleteOneDeck(deckToDelete :Deck) :Flow<DataResult<Int>>{
        return flow {
            when(val deleteLine = deckDao.deleteOneDeck(deckToDelete)){
                1 -> emit(DataResult.Success(deleteLine))// 只有1是正常删除
                else ->emit(DataResult.Error("删除失败，实际删除了${deleteLine}行"))
            }
        }.catch {
            emit(DataResult.Error(it.message!!))
        }.flowOn(Dispatchers.IO)
    }

    /*-------------------------------------卡组操作方法结束--------------------------------------------------*/

    /*------------------------------------这里写OriginMove操作方法---------------------------------------*/

    /**根据Id列表获取招式列表，按idList顺序输出*/
    suspend fun getOriginListByIdList(idList: List<Int>):MutableList<MoveOrigin>{
        val tempMap = idList.associateWith { it }
        val result = moveOriginDAO.getMovesByIds(idList)
        val finalResult = mutableListOf<MoveOrigin>()
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
    suspend fun getMoveById(id :Int):MoveOrigin{
        return moveOriginDAO.getMoveById(id)
    }

    /**起始站架筛选招式*/
    suspend fun getMoveListByStartSide(start :StandSide) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByStartSide(start)
    }

    /**结束站架筛选招式*/
    suspend fun getOriginListByEndSide(end :StandSide) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByEndSide(end)
    }

    /**攻击朝向筛选*/
    suspend fun getOriginListByAttackToward(toward: AttackToward) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByAttackToward(toward)
    }

    /**攻击高度筛选*/
    suspend fun getOriginListByAttackAltitude(altitude: AttackAltitude) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByAttackAltitude(altitude)
    }

    /**攻击走向筛选*/
    suspend fun getOriginListByAttackDirection(direction: AttackDirection) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByAttackDirection(direction)
    }

    /**招式特效筛选*/
    suspend fun getOriginListByMoveEffect(effect: MoveEffect) :List<MoveOrigin>{
        return moveOriginDAO.getMovesByEffect(effect)
    }

    /*------------------------------------OriginMove操作方法结束---------------------------------------*/

}