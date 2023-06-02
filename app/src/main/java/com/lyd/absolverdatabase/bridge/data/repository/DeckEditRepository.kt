package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import kotlinx.coroutines.*

class DeckEditRepository(private val deckDao: DeckDAO, // 用于保存编辑完的卡组
                         private val moveOriginDAO: MoveOriginDAO, // 和下面的dao一样用于查询招式
                         private val moveGPDAO: MoveGPDAO) {

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

}