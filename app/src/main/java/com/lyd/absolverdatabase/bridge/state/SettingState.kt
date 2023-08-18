package com.lyd.absolverdatabase.bridge.state

import androidx.annotation.IntRange
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.logUtils.LLog
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
    val showSeqDetailWhenSharedDeckFlow :StateFlow<Boolean> = repository.isShowSeqDetailWhenSharedDeckPreference.asFlow().map {
        it ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),false)
    fun changeShowSeqDetailWhenSharedDeck(boolean: Boolean){
        repository.isShowSeqDetailWhenSharedDeck = boolean
        viewModelScope.launch {
            repository.isShowSeqDetailWhenSharedDeckPreference.set { boolean }
        }
    }
    val recordCrashMsgFlow :StateFlow<Boolean> = repository.isRecordCrashMsgPreference.asFlow().map {
        it ?: true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),true)
    fun changeRecordCrashMsg(boolean: Boolean){
        repository.isRecordCrashMsg = boolean
        viewModelScope.launch {
            repository.isRecordCrashMsgPreference.set { boolean }
        }
    }
    val logPrintLevelFlow :StateFlow<Int> = repository.logPrintLevelPreference.asFlow().map {
        it ?: 3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),3)
    fun changeLogPrintLevel(@IntRange(from = 1, to = 6) level: Int){
        repository.logPrintLevel = level
        viewModelScope.launch {
            repository.logPrintLevelPreference.set { level }
        }
    }
    val logWriteLevelFlow :StateFlow<Int> = repository.logWriteLevelPreference.asFlow().map {
        it ?: 4
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),4)
    fun changeLogWriteLevel(@IntRange(from = 3,to = 6) level: Int){
        repository.logWriteLevel = level
        viewModelScope.launch {
            repository.logWriteLevelPreference.set { level }
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