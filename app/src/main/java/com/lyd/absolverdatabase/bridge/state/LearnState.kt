package com.lyd.absolverdatabase.bridge.state

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.repository.BilibiliRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LearnState(private val repository: BilibiliRepository, private val state :SavedStateHandle) : ViewModel() {

    private val TAG = "${javaClass.simpleName}-${javaClass.hashCode()}"

    val videoSharedFlow : MutableSharedFlow<List<BilibiliVideo>> = MutableSharedFlow(replay = 1/*, extraBufferCapacity = 1*/)
    /**
     * @param isManualRefresh 是否手动刷新，默认为 false
     * */
    suspend fun getVideoList(map: MutableMap<String,String>,isManualRefresh :Boolean = false){
        viewModelScope.launch {
            repository.getListFlow(map,isManualRefresh).collectLatest {
                when(it){
                    is DataResult.Success ->{
                        videoSharedFlow.emit(it.data)
                    }
                    is DataResult.Error ->{
                        Log.e(TAG, "getVideoList: ${it.error}")
                    }
                }
            }
        }
    }

    suspend fun getCookie() :Flow<String>{
        return repository.getBaseCookie()
    }

}

class LearnViewModelFactory(private val repository: BilibiliRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(LearnState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return LearnState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}