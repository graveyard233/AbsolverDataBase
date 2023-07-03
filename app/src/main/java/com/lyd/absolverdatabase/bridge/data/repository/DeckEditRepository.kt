package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.bean.RepoResult
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveCEDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeckEditRepository(private val deckDao: DeckDAO, // 用于保存编辑完的卡组
                         private val moveOriginDAO: MoveOriginDAO, // 和下面的dao一样用于查询招式
                         private val moveCEDAO: MoveCEDAO) {

    private val TAG = javaClass.simpleName


     suspend fun getOriginListByIds(idList: List<Int>):List<MoveOrigin?>{
         return withContext(Dispatchers.IO){
             val move0 = async{
                 if (idList[0] != -1){
                     moveOriginDAO.getMoveById(idList[0])
                 } else{
                     null
                 }
             }
             val move1 = async{
                 if (idList[1] != -1){
                     moveOriginDAO.getMoveById(idList[1])
                 } else{
                     null
                 }
             }
             val move2 = async{
                 if (idList[2] != -1){
                     moveOriginDAO.getMoveById(idList[2])
                 } else{
                     null
                 }
             }
             listOf(move0.await(),move1.await(),move2.await())
         }

//        val result = moveOriginDAO.getMovesByIds(idList)
//        val finalResult = mutableListOf<MoveOrigin>()
//        // 筛出来之后重排
//        idList.onEachIndexed { idIndex, id ->
//            flag@for (move in result){
//                if (move.id == id){
//                    finalResult.add(idIndex,move)
//                    break@flag
//                }
//            }
//        }
//        return finalResult
    }

    suspend fun getOriginMoveById(id :Int):MoveOrigin?{
        return withContext(Dispatchers.IO){
            if (id >= 0){
                moveOriginDAO.getMoveById(id)
            } else{
                null
            }
        }
    }

    // 不论是徒手招式还是剑卡招式，都不需要关心它的镜像是否能用，全部依赖传入的MoveBox的isMirror来判断这个招式要不要转成镜像
    suspend fun getOriginsWithMirrorByBoxes(boxes :MutableList<MoveBox>) :MutableList<MoveBox>{
        return withContext(Dispatchers.IO){
            val box0 = async {
                if (boxes[0].moveId != -1){
                    val tempMove = moveOriginDAO.getMoveById(boxes[0].moveId)
                    if (boxes[0].isUseMirror != 0){
                        MoveBox(boxes[0].moveId, isUseMirror = boxes[0].isUseMirror).apply {
                            this.moveOrigin = tempMove.apply { this.toMirror() }
                        }
                    } else {
                        MoveBox(boxes[0].moveId, isUseMirror = boxes[0].isUseMirror).apply { moveOrigin = tempMove }
                    }
                } else{
                    MoveBox()
                }
            }
            val box1 = async {
                if (boxes[1].moveId != -1){
                    val tempMove = moveOriginDAO.getMoveById(boxes[1].moveId)
                    if (boxes[1].isUseMirror != 0){
                        MoveBox(boxes[1].moveId, isUseMirror = boxes[1].isUseMirror).apply {
                            this.moveOrigin = tempMove.apply { this.toMirror() }
                        }
                    } else {
                        MoveBox(boxes[1].moveId, isUseMirror = boxes[1].isUseMirror).apply { moveOrigin = tempMove }
                    }
                } else{
                    MoveBox()
                }
            }
            val box2 = async {
                if (boxes[2].moveId != -1){
                    val tempMove = moveOriginDAO.getMoveById(boxes[2].moveId)
                    if (boxes[2].isUseMirror != 0){
                        MoveBox(boxes[2].moveId, isUseMirror = boxes[2].isUseMirror).apply {
                            this.moveOrigin = tempMove.apply { this.toMirror() }
                        }
                    } else {
                        MoveBox(boxes[2].moveId, isUseMirror = boxes[2].isUseMirror).apply { moveOrigin = tempMove }
                    }
                } else{
                    MoveBox()
                }
            }
            mutableListOf(box0.await(),box1.await(),box2.await())
        }
    }

    suspend fun getOriginWithMirrorByBox(box :MoveBox) :MoveBox{
        return withContext(Dispatchers.IO){
            if (box.moveId != -1){
                val tempMove = moveOriginDAO.getMoveById(box.moveId)
                if (box.isUseMirror != 0){
                    MoveBox(box.moveId, isUseMirror = box.isUseMirror).apply { this.moveOrigin = tempMove.apply { this.toMirror() } }
                } else {
                    MoveBox(box.moveId, 0).apply { this.moveOrigin = tempMove }
                }
            } else {
                MoveBox()
            }
        }
    }

    suspend fun saveDeckIntoDatabase(deck :Deck): Flow<RepoResult<String>> {
        return flow<RepoResult<String>> {
            val result = deckDao.upsertDeck(deck)// 拿到的是操作的id，假如是更新replace，则拿到的是已经插入的id，假如是实打实的插入，则拿到的是插入的新id
            Log.i(TAG, "saveDeckIntoDatabase: 触发了更新或插入操作 $result")
            if (result >= 0){
                emit(RepoResult.RpSuccess(result.toString()))
            } else {
                emit(RepoResult.RpError("操作失败:$result"))
            }
        }.catch {
            emit(RepoResult.RpError(it.message!!))
        }.flowOn(Dispatchers.IO)
    }

}