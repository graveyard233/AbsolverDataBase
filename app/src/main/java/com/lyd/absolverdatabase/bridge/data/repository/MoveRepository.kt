package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.bean.RepoResult
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MoveRepository(private val moveOriginDAO: MoveOriginDAO, // 和下面的dao一样用于查询招式
                     private val moveGPDAO: MoveGPDAO)
{
    suspend fun getOriginListByStartSide(sideInt :Int) : RepoResult<List<MoveOrigin>> {
        val tempList = moveOriginDAO.getMovesByStartSide(SideUtil.getSideByInt(sideInt))
        if (tempList.isEmpty()){
            return RepoResult.RpEmpty("list is empty")
        } else {
            return RepoResult.RpSuccess(tempList)
        }

    }
}