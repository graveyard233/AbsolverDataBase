package com.lyd.absolverdatabase.bridge.state

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.DataResult
import com.lyd.absolverdatabase.bridge.data.repository.SettingDatabaseRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingDatabaseState(private val repository: SettingDatabaseRepository,private val state :SavedStateHandle) :ViewModel() {

    fun syncOriginTable(whenFinish :()->Unit = {}){
        viewModelScope.launch {
            repository.syncMoveOriginTable()
            whenFinish.invoke()
        }
    }
    fun syncCETable(whenFinish :()->Unit = {}){
        viewModelScope.launch {
            repository.syncMoveCETableFromLocal()
            whenFinish.invoke()
        }
    }

    fun syncCETableFromWebView(html :String,startTime :Long,whenError: (String) -> Unit = {}, whenSuccess: (Long) -> Unit = {},whenFinish: () -> Unit){
        viewModelScope.launch {
            repository.syncMoveCETableFromWebViewFlow(html,startTime).collectLatest {
                when(it){
                    is DataResult.Error -> {
                        whenError.invoke(it.error)
                    }
                    is DataResult.Success -> {
                        whenSuccess.invoke(it.data)
                    }
                }
                whenFinish.invoke()
            }
        }
    }
}

class SettingDatabaseViewModelFactory(private val repository: SettingDatabaseRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SettingDatabaseState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SettingDatabaseState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}