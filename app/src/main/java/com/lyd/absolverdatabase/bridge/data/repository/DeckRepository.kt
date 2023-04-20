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
    private val moveList = mutableListOf<Move>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            moveJsDao.deleteAll()
            deckDao.deleteAll()



            moveJsDao.upsertAll(MoveGenerate.generateMoveJsons())

            val tempList = moveJsDao.getAllMove()
            tempList.forEachIndexed { index, moveJson ->
                Log.i(TAG, "$index: moveJson -> ${moveJson.json}")
            }

            val move = GsonUtils.fromJson<Move>(tempList[0].json,GsonUtils.getType(Move::class.java))
            Log.i(TAG, "move_0: $move")

            deckDao.upsertAll(DeckGenerate.generateDeck())

            Log.i(TAG, "decks: ${GsonUtils.toJson(deckDao.getAllDeck())}")
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

}