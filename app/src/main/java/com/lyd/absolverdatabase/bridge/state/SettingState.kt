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
    val showMoreMoveCEInfoFlow :StateFlow<Boolean> = repository.isShowMoreMoveCEInfoPreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),false)
    fun changeShowMoreMoveCEInfo(boolean: Boolean){
        repository.isShowMoreMoveCEInfo = boolean
        viewModelScope.launch {
            repository.isShowMoreMoveCEInfoPreference.set { boolean }
        }
    }
    val useNightModeFlow :StateFlow<Boolean> = repository.useNightModePreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),false)
    fun changeUseNightMode(boolean: Boolean){
        repository.useNightMode = boolean
        viewModelScope.launch {
            repository.useNightModePreference.set { boolean }
        }
    }
    val useWhatThemeFlow :StateFlow<Int> = repository.useWhatThemePreference.asFlow().map {
        it ?: 1
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),1)
    fun changeUseWhatTheme(whatTheme :Int){
        repository.useWhatTheme = whatTheme
        viewModelScope.launch {
            repository.useWhatThemePreference.set { whatTheme }
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