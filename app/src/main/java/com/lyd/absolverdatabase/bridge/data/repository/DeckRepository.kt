package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveDAO
import com.lyd.absolverdatabase.utils.DeckGenerate
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.MoveGenerate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeckRepository(private val deckDao: DeckDAO,private val moveDao: MoveDAO) {

    private val TAG = javaClass.simpleName

    init {
        CoroutineScope(Dispatchers.IO).launch {
            moveDao.deleteAll()
            deckDao.deleteAll()

            moveDao.upsertAll(MoveGenerate.generateMoves())

            Log.i(TAG, "moves: ${GsonUtils.toJson(moveDao.getAllMove())}")

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