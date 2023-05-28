package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.bean.RepoResult
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.SideUtil

class MoveRepository(private val moveOriginDAO: MoveOriginDAO, // 和下面的dao一样用于查询招式
                     private val moveGPDAO: MoveGPDAO)
{
    private val TAG = javaClass.simpleName
    suspend fun getOriginListByStartSide(sideInt :Int) : RepoResult<List<MoveOrigin>> {
        val tempList = moveOriginDAO.getMovesByStartSide(SideUtil.getSideByInt(sideInt))
        if (tempList.isEmpty()){
            return RepoResult.RpEmpty("list is empty")
        } else {
            return RepoResult.RpSuccess(tempList)
        }

    }

    suspend fun getOriginHandListByEndSide(sideInt: Int) :RepoResult<List<MoveOrigin>> {
        val tempList = moveOriginDAO.getHandMoveByEndSide(SideUtil.getSideByInt(sideInt))
        if (tempList.isEmpty()){
            return RepoResult.RpEmpty("handList is empty")
        } else {
            return RepoResult.RpSuccess(tempList)
        }
    }
    suspend fun getOriginSwordListByEndSide(sideInt: Int) :RepoResult<List<MoveOrigin>> {
        val tempList = moveOriginDAO.getSwordMoveByEndSide(SideUtil.getSideByInt(sideInt))
        if (tempList.isEmpty()){
            return RepoResult.RpEmpty("SwordList is empty")
        } else {
            return RepoResult.RpSuccess(tempList)
        }
    }
}