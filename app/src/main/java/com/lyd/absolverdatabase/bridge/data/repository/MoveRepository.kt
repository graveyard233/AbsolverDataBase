package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveCNDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.SideUtil

class MoveRepository(private val moveOriginDAO: MoveOriginDAO, // 和下面的dao一样用于查询招式
                     private val moveCNDAO: MoveCNDAO)
{
    private val TAG = javaClass.simpleName

    // 根据起始站架来分辨镜像数据，并且做好做镜像处理，从这里出去的数据可以直接使用，但它是混了镜像数据，所以最好谨慎地（甚至是别）放到外部使用
    suspend fun getHandOriginWithMirror(startInt: Int?, endInt: Int) :RepoResult<List<MoveForSelect>>{
        var result :List<MoveOrigin> = listOf()
        val mirrorIdList = mutableListOf<Int>()
        // 先获取到包含起始站架和镜像起始站架的所有招式，这里要按有没有起始站夹限制来区分使用哪一个DAO函数
        if (startInt != null){// 起始站架被限制，我需要按起始站架及镜像来搜索徒手招式
            Log.i(TAG, "getHandOriginWithMirror: 查询的起始站架是 ${SideUtil.getSideByInt(startInt)} 镜像站架是 ${SideUtil.getMirrorSide(startInt)} 结束站架是 ${SideUtil.getSideByInt(endInt)}")
            val tempList = moveOriginDAO.getHandMoveByStartWithMirror(SideUtil.getSideByInt(startInt), mirrorStartSide = SideUtil.getMirrorSide(startInt))
            // 然后把其中是镜像站架的招式进行全部进行镜像操作
            result = tempList.map {before ->
                if (before.startSide == SideUtil.getMirrorSide(startInt)) {// 只改镜像的招式
//                        Log.i(TAG, "修改前的: $before")
                    before.startSide = SideUtil.getMirrorSide(before.startSide)
                    before.endSide = SideUtil.getMirrorSide(before.endSide)
                    before.attackToward = AttackToward.getMirrorToward(before.attackToward)
//                        Log.i(TAG, "修改后的: $before")
                    if (before.endSide == SideUtil.getSideByInt(endInt)){
                        mirrorIdList.add(before.id)// 标记镜像使用的招式id，而且这个招式绝对是可用的，而不会被下面的filter筛调；这么做的原因是筛出真正可用的镜像招式，防止下面的contains对比太多次
                    }
                }
                before
            }.filter {// 筛选出符合结束站架的招式
                it.endSide == SideUtil.getSideByInt(endInt)
            }
        } else {// 没有起始站架的限制，所以直接按结束站架来筛选，筛出来的可以直接使用,但还是要检查镜像站架并变化
            val tempEndSide: StandSide = SideUtil.getSideByInt(endInt)
            val tempList = moveOriginDAO.getHandMoveByEndWithMirror(tempEndSide, mirrorEndSide = SideUtil.getMirrorSide(endInt))
            result = tempList.map {// 把所有是镜像才能用的招式变化，因为起始站架无限制，所以这个list就已经完全可用了
                if (it.endSide == SideUtil.getMirrorSide(endInt)) {
                    it.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(it.startSide))
                    it.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(it.endSide))
                    it.attackToward = AttackToward.getMirrorToward(it.attackToward)

                    mirrorIdList.add(it.id)
                }
                it
            }
        }
        return if (result.isEmpty()){
            RepoResult.RpEmpty("handList is empty")
        } else {
            RepoResult.RpSuccess(result.map {
                MoveForSelect(moveOrigin = it, isSelected = false, isMirror = if (mirrorIdList.contains(it.id)) 1 else 0)
            })
        }
    }

    suspend fun getSwordOriginWithMirror(startInt: Int?, endInt: Int): RepoResult<List<MoveForSelect>> {
        var result :List<MoveOrigin> = listOf()
        val swordList = moveOriginDAO.getSwordMove()// 先筛选出剑卡中可用的招式
        val mirrorIdList = mutableListOf<Int>()
        // 因为剑卡组有些招式不能镜像使用，所以我需要先筛出可用在剑卡中的招式，然后再根据镜像可用的条件来改变招式数据
        if (startInt != null){// 起始站架被限制，我需要按照起始站架来进行第一次筛选
            val tempStartSide = SideUtil.getSideByInt(startInt)
            result = swordList.filter {origin: MoveOrigin ->
                if (origin.canOriginSword && !origin.canMirrorSword){// 仅原版可用
                    origin.startSide == tempStartSide
                } else if (!origin.canOriginSword && origin.canMirrorSword){// 仅镜像可用，在确定后要把招式直接修改了，不麻烦在下一层再改
                    if (SideUtil.getMirrorInt(origin.startSide) == startInt){// 如果招式的镜像起始站架等于被限定的起始站架，则相当于这个镜像招式可用，可以修改数据
                        origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                        origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                        origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                        mirrorIdList.add(origin.id)// 标记镜像使用的招式
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
                            origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                            origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                            origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                            mirrorIdList.add(origin.id)// 标记镜像使用的招式
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
            result = swordList.filter { origin: MoveOrigin ->
                if (origin.canOriginSword && !origin.canMirrorSword){// 仅原版可用
                    origin.endSide == tempEndSide
                } else if (!origin.canOriginSword && origin.canMirrorSword){// 仅镜像可用，在确定后要把招式直接修改了，不麻烦在下一层再改
                    if (SideUtil.getMirrorInt(origin.endSide) == endInt){// 如果招式的镜像结束站架等于被限定的结束站架，则相当于这个镜像招式可用，可以修改数据
                        origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                        origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                        origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                        mirrorIdList.add(origin.id)// 标记镜像使用的招式
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
                            origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                            origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                            mirrorIdList.add(origin.id)// 标记镜像使用的招式
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
        return if (result.isEmpty()){
            RepoResult.RpEmpty("swordList is empty")
        } else {
            RepoResult.RpSuccess(result.map {
                MoveForSelect(moveOrigin = it, isSelected = false, isMirror = if (mirrorIdList.contains(it.id)) 1 else 0)
            })
        }
    }

    /**寻找自选序列可用的招式，起始站架不能和结束站架相同*/
    suspend fun getHandOriginOptWithMirror(startInt: Int, endInt: Int) :RepoResult<List<MoveForSelect>>{
        val canNotEndInSide = SideUtil.getSideByInt(startInt)
        val endSide = SideUtil.getSideByInt(endInt)
        var result = listOf<MoveOrigin>()
        val mirrorIdList = mutableListOf<Int>()
        // 先获取起始针对起始站架筛选的招式，这些招式的起始站架或者是镜像的起始站架都是符合起始站架限制的
        val tempList = moveOriginDAO.getHandMoveByStartWithMirror(SideUtil.getSideByInt(startInt), mirrorStartSide = SideUtil.getMirrorSide(startInt))
        result = tempList.map {before ->// 然后把其中是镜像站架的招式进行全部进行镜像操作
            if (before.startSide == SideUtil.getMirrorSide(startInt)){// 只改镜像的招式
                before.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(before.startSide))
                before.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(before.endSide))
                before.attackToward = AttackToward.getMirrorToward(before.attackToward)
                if (before.endSide != canNotEndInSide){
                    mirrorIdList.add(before.id)
                }
            }
            before
        }.filter {// 然后从这些招式中，筛选出结束站架不等于起始站架的
            it.endSide != canNotEndInSide && it.endSide == endSide
        }
        return if (result.isEmpty()){
            RepoResult.RpEmpty("optHand is empty")
        } else {
            RepoResult.RpSuccess(data = result.map {
                MoveForSelect(moveOrigin = it, isSelected = false, isMirror = if (mirrorIdList.contains(it.id)) 1 else 0)
            })
        }
    }

    suspend fun getSwordOriginOptWithMirror(startInt: Int, endInt: Int) :RepoResult<List<MoveForSelect>>{
        var result :List<MoveOrigin> = listOf()
        val swordList = moveOriginDAO.getSwordMove()// 先筛选出剑卡中可用的招式
        val mirrorIdList = mutableListOf<Int>()
        val tempStartSide = SideUtil.getSideByInt(startInt)
        val endSide = SideUtil.getSideByInt(endInt)
        result = swordList.filter {origin: MoveOrigin ->
            if (origin.canOriginSword && !origin.canMirrorSword){// 仅原版可用
                origin.startSide == tempStartSide
            } else if (!origin.canOriginSword && origin.canMirrorSword){// 仅镜像可用，在确定后要把招式直接修改了，不麻烦在下一层再改
                if (SideUtil.getMirrorInt(origin.startSide) == startInt){// 如果招式的镜像起始站架等于被限定的起始站架，则相当于这个镜像招式可用，可以修改数据
                    origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                    origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                    origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                    mirrorIdList.add(origin.id)
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
                        origin.startSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.startSide))
                        origin.endSide = SideUtil.getMirrorSide(SideUtil.getIntBySide(origin.endSide))
                        origin.attackToward = AttackToward.getMirrorToward(origin.attackToward)
                        mirrorIdList.add(origin.id)
                        true
                    }
                    else -> {
                        false
                    }
                }
            } else {// 两个都不行 但这个其实不用判断，因为在room查询的时候已经过滤过不可在剑卡中用的招式了
                false
            }
        }.filter {
            it.endSide != tempStartSide && it.endSide == endSide
        }
        return if (result.isEmpty()){
            RepoResult.RpEmpty("handList is empty")
        } else {
            RepoResult.RpSuccess(result.map {
                MoveForSelect(moveOrigin = it, isSelected = false, isMirror = if (mirrorIdList.contains(it.id)) 1 else 0)
            })
        }
    }
}