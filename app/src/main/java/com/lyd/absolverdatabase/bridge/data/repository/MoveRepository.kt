package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.AttackToward
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.bean.RepoResult
import com.lyd.absolverdatabase.bridge.data.bean.StandSide
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    // 根据起始站架来分辨镜像数据，并且做好做镜像处理，从这里出去的数据可以直接使用，但它是混了镜像数据，所以最好谨慎地（甚至是别）放到外部使用
    suspend fun getHandOriginWithMirror(startInt: Int?, endInt: Int) :RepoResult<List<MoveOrigin>>{
        var result :List<MoveOrigin> = listOf()
        // 先获取到包含起始站架和镜像起始站架的所有招式，这里要按有没有起始站夹限制来区分使用哪一个DAO函数
        if (startInt != null){// 起始站架被限制，我需要按起始站架及镜像来搜索徒手招式
            val tempList = moveOriginDAO.getHandMoveByStartWithMirror(SideUtil.getSideByInt(startInt), mirrorStartSide = SideUtil.getMirrorSide(startInt))
            // 然后把其中是镜像站架的招式进行全部进行镜像操作
            result = withContext(Dispatchers.Default){// 在CPU密集型线程中操作
                tempList.map {
                    if (it.startSide == SideUtil.getMirrorSide(startInt)){// 只改镜像的招式
                        it.startSide = SideUtil.getMirrorSide(startInt)
                        it.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(it.endSide))
                        it.attackToward = AttackToward.getMirrorToward(it.attackToward)
                    }
                    it
                }.filter {// 筛选出符合结束站架的招式
                    it.endSide == SideUtil.getSideByInt(endInt)
                }
            }
        } else {// 没有起始站架的限制，所以直接按结束站架来筛选，筛出来的可以直接使用
            result = moveOriginDAO.getHandMoveByEndSide(SideUtil.getSideByInt(endInt))
        }
        return if (result.isEmpty()){
            RepoResult.RpEmpty("handList is empty")
        } else {
            RepoResult.RpSuccess(result)
        }
    }

    suspend fun getSwordOriginWithMirror(startInt: Int?, endInt: Int): RepoResult<List<MoveOrigin>> {
        var result :List<MoveOrigin> = listOf()
        val swordList = moveOriginDAO.getSwordMove()// 先筛选出剑卡中可用的招式
        // 因为剑卡组有些招式不能镜像使用，所以我需要先筛出可用在剑卡中的招式，然后再根据镜像可用的条件来改变招式数据
        if (startInt != null){// 起始站架被限制，我需要按照起始站架来进行第一次筛选
            val tempStartSide = SideUtil.getSideByInt(startInt)
            swordList.filter {origin: MoveOrigin ->
                if (origin.canOriginSword && !origin.canMirrorSword){// 仅原版可用
                    origin.startSide == tempStartSide
                } else if (!origin.canOriginSword && origin.canMirrorSword){// 仅镜像可用，在确定后要把招式直接修改了，不麻烦在下一层再改
                    if (SideUtil.getMirrorInt(origin.startSide) == startInt){// 如果招式的镜像起始站架等于被限定的起始站架，则相当于这个镜像招式可用，可以修改数据
                        origin.startSide = SideUtil.getMirrorSide(startInt)
                        origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                        origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                        true
                    } else {
                        false
                    }
                } else if (origin.canOriginSword && origin.canMirrorSword){// 两者皆可使用
                    when (origin.startSide) {
                        tempStartSide -> {// 原版招式的起始站架就已经符合限定的站架，不需要修改
                            true
                        }
                        SideUtil.getMirrorSide(startInt) -> {// 如果起始站架和镜像的限定起始站架相等，则相当于这个镜像招式才是可用的，要修改数据
                            origin.startSide = SideUtil.getMirrorSide(startInt)
                            origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                            origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                } else {// 两个都不行 但这个其实不用判断，因为在room查询的时候已经过滤过不可在剑卡中用的招式了
                    false
                }
            }.filter { origin: MoveOrigin -> // 这里已经过了起始站架筛选，且根据不同条件进行了镜像变化，接下来进行结束站架的筛选
                origin.endSide == SideUtil.getSideByInt(endInt)
            }
        } else{// 起始站架没有被限制，需要按结束站架来筛选，但这和上面一样要检查镜像
            val tempEndSide :StandSide = SideUtil.getSideByInt(endInt)
            swordList.filter { origin: MoveOrigin ->
                if (origin.canOriginSword && !origin.canMirrorSword){// 仅原版可用
                    origin.endSide == tempEndSide
                } else if (!origin.canOriginSword && origin.canMirrorSword){// 仅镜像可用，在确定后要把招式直接修改了，不麻烦在下一层再改
                    if (SideUtil.getMirrorInt(origin.endSide) == endInt){// 如果招式的镜像结束站架等于被限定的结束站架，则相当于这个镜像招式可用，可以修改数据
                        origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                        origin.endSide = SideUtil.getMirrorSide(endInt)
                        origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                        true
                    } else {
                        false
                    }
                } else if (origin.canOriginSword && origin.canMirrorSword){// 两者皆可使用
                    when(origin.endSide){
                        tempEndSide ->{// 原版招式的结束站架就已经符合限定的站架，不需要修改
                            true
                        }
                        SideUtil.getMirrorSide(endInt) ->{// 如果结束站架和镜像的限定结束站架相等，则相当于这个镜像招式才是可用的，要修改数据
                            origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                            origin.endSide = SideUtil.getMirrorSide(endInt)
                            origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                            true
                        }
                        else ->{
                            false
                        }
                    }
                } else {// 两个都不行 但这个其实不用判断，因为在room查询的时候已经过滤过不可在剑卡中用的招式了
                    false
                }
            }
        }
        TODO()
    }
}