package com.lyd.absolverdatabase.bridge.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.data.bean.RepoResult
import com.lyd.absolverdatabase.bridge.data.repository.MoveRepository


class MoveRecycleState(private val repository: MoveRepository,
                       private val state :SavedStateHandle) :ViewModel()
{

    private val TAG = "${javaClass.simpleName}-${javaClass.hashCode()}"

    // 根据起始站架以及镜像来筛选所有招式
    suspend fun originListWithMirror(startInt: Int?,endInt: Int,canHand: Boolean = false) :List<MoveForSelect>
    {
        val result = if (canHand){
            repository.getHandOriginWithMirror(startInt,endInt)
        } else {
            repository.getSwordOriginWithMirror(startInt,endInt)
        }
        return when(result){
            is RepoResult.RpEmpty -> listOf<MoveForSelect>()
            is RepoResult.RpError -> listOf<MoveForSelect>()
            is RepoResult.RpSuccess -> result.data
        }
    }

    suspend fun originOptListWithMirror(startInt: Int,endInt: Int,canHand: Boolean = false) :List<MoveForSelect>
    {
        val result = if (canHand){
            repository.getHandOriginOptWithMirror(startInt,endInt)
        } else {
            repository.getSwordOriginOptWithMirror(startInt,endInt)
        }
        return when(result){
            is RepoResult.RpEmpty -> listOf<MoveForSelect>()
            is RepoResult.RpError -> listOf<MoveForSelect>()
            is RepoResult.RpSuccess -> result.data
        }
    }
}

class MoveRecycleViewModelFactory(private val repository: MoveRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(MoveRecycleState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return MoveRecycleState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}