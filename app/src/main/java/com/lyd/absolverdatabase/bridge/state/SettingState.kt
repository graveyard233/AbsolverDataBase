package com.lyd.absolverdatabase.bridge.state

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingState(private val repository :SettingRepository, private val state : SavedStateHandle) : ViewModel() {
    private val TAG = javaClass.simpleName

    val dialogGaussianBlurFlow :StateFlow<Boolean> = repository.isDialogGaussianBlurPreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),true)
    fun changeGaussianBlurFlow(boolean: Boolean){
        repository.isDialogGaussianBlur = boolean
        viewModelScope.launch {
            repository.isDialogGaussianBlurPreference.set { boolean }
        }
    }
    val askBeforeImportDeckFlow :StateFlow<Boolean> = repository.isNeedAskBeforeImportPreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),true)
    fun changeAskBeforeImportDeck(boolean: Boolean){
        repository.isNeedAskBeforeImport = boolean
        viewModelScope.launch{
            repository.isNeedAskBeforeImportPreference.set { boolean }
        }
    }
    val useCNEditionModFlow :StateFlow<Boolean> = repository.isUseCNEditionModPreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),false)
    fun changeUseCNEditionMod(enable: Boolean){
        repository.isUseCNEditionMod = enable
        viewModelScope.launch {
            repository.isUseCNEditionModPreference.set { enable }
        }
    }
}

class SettingViewModelFactory(private val repository: SettingRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SettingState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SettingState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}