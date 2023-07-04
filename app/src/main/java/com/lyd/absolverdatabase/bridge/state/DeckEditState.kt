package com.lyd.absolverdatabase.bridge.state

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.DeckEditRepository
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.ui.page.DeckEditFragment
import com.lyd.absolverdatabase.ui.page.MoveSelectFragment
import com.lyd.absolverdatabase.utils.DeckGenerate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**用于[DeckEditFragment]和[MoveSelectFragment]，在这里写的函数要标注好给哪个fragment用*/
class DeckEditState(private val repository: DeckEditRepository,
                    private val savedState: SavedStateHandle) :ViewModel()
{
    private val TAG = "${javaClass.simpleName}-${hashCode()}"

    fun fromDeckToEdit(){
        savedState["deckInSaved"] = DeckGenerate.generateEmptyDeck(isFromDeckToEdit = true)
    }

    fun getDeckInSaved() : Deck? = savedState.get<Deck>("deckInSaved")
    fun saveDeckInSaved(deck: Deck,
                        isForSave :Boolean = false,
                        ifError :(String)->Unit = {},
                        ifSuccess :(String)->Unit = {}) {

        // 保存的时候应该还要触发序列sharedFlow的变化，前面这句话应该不需要，因为一旦改变所有的都会跟着一起动，序列数据已经变了，所以不需要触发序列变化
        if (isForSave){
            viewModelScope.launch {
                repository.saveDeckIntoDatabase(deck = deck).collectLatest {
                    when(it){
                        is RepoResult.RpEmpty -> {}
                        is RepoResult.RpError -> {
                            ifError.invoke(it.error)
                        }
                        is RepoResult.RpSuccess -> {
//                            ifSuccess.invoke(it.data)
                            ifSuccess.invoke(it.data)
                            savedState.set("deckInSaved", deck.apply { deckId = it.data.toInt() })
                        }
                    }
                }
            }
        } else {
            savedState.set("deckInSaved", deck)
        }
    }

    val deckInSaved = savedState.getStateFlow("deckInSaved",DeckGenerate.generateEmptyDeck())
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 0
        )
    fun saveDeckFromShared(deck: Deck,
                           ifError: (String) -> Unit = {},
                           ifSuccess: (String) -> Unit = {}){
        viewModelScope.launch {
            repository.saveDeckIntoDatabase(deck = deck).collectLatest {
                when(it){
                    is RepoResult.RpEmpty -> {}
                    is RepoResult.RpError -> {
                        ifError.invoke(it.error)
                    }
                    is RepoResult.RpSuccess -> {
                        ifSuccess.invoke(it.data)
                    }
                }
            }
        }
    }




    val optUpperRight :MutableSharedFlow<MoveBox> = MutableSharedFlow()
    val optUpperLeft :MutableSharedFlow<MoveBox> = MutableSharedFlow()
    val optLowerLift :MutableSharedFlow<MoveBox> = MutableSharedFlow()
    val optLowerRight :MutableSharedFlow<MoveBox> = MutableSharedFlow()

    private val _sequenceUpperRight :MutableSharedFlow<MutableList<MoveBox>> = MutableSharedFlow()
    private val _sequenceUpperLeft :MutableSharedFlow<MutableList<MoveBox>> = MutableSharedFlow()
    private val _sequenceLowerLeft :MutableSharedFlow<MutableList<MoveBox>> = MutableSharedFlow()
    private val _sequenceLowerRight :MutableSharedFlow<MutableList<MoveBox>> = MutableSharedFlow()

    val sequenceUpperRight = _sequenceUpperRight.asSharedFlow()
    val sequenceUpperLeft  = _sequenceUpperLeft .asSharedFlow()
    val sequenceLowerLeft  = _sequenceLowerLeft .asSharedFlow()
    val sequenceLowerRight = _sequenceLowerRight.asSharedFlow()

    suspend fun updateSeqUpperRight(list :MutableList<MoveBox>) = _sequenceUpperRight.emit(list)
    suspend fun updateSeqUpperLeft(list :MutableList<MoveBox>) = _sequenceUpperLeft.emit(list)
    suspend fun updateSeqLowerLeft(list :MutableList<MoveBox>) = _sequenceLowerLeft.emit(list)
    suspend fun updateSeqLowerRight(list :MutableList<MoveBox>) = _sequenceLowerRight.emit(list)

    fun updateAllOption(deck: Deck){
        viewModelScope.launch(Dispatchers.IO){
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    optUpperRight.emit(repository.getCEWithMirrorByBox(deck.optionalUpperRight))
                } else {
                    optUpperRight.emit(repository.getOriginWithMirrorByBox(deck.optionalUpperRight))
                }
            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    optUpperLeft.emit(repository.getCEWithMirrorByBox(deck.optionalUpperLeft))
                } else {
                    optUpperLeft.emit(repository.getOriginWithMirrorByBox(deck.optionalUpperLeft))
                }
            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    optLowerLift.emit(repository.getCEWithMirrorByBox(deck.optionalLowerLeft))
                } else {
                    optLowerLift.emit(repository.getOriginWithMirrorByBox(deck.optionalLowerLeft))
                }

            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    optLowerRight.emit(repository.getCEWithMirrorByBox(deck.optionalLowerRight))
                } else {
                    optLowerRight.emit(repository.getOriginWithMirrorByBox(deck.optionalLowerRight))
                }
            }
        }
    }

    fun updateAllSequence(deck: Deck){// 千万不要修改这个deck的属性
        viewModelScope.launch/*(Dispatchers.IO)*/{
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    updateSeqUpperRight(repository.getCEsWithMirrorByBoxes(deck.sequenceUpperRight))
                } else {
                    updateSeqUpperRight(repository.getOriginsWithMirrorByBoxes(deck.sequenceUpperRight))
                }
            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    updateSeqUpperLeft(repository.getCEsWithMirrorByBoxes(deck.sequenceUpperLeft))
                } else {
                    updateSeqUpperLeft(repository.getOriginsWithMirrorByBoxes(deck.sequenceUpperLeft))
                }
            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    updateSeqLowerLeft(repository.getCEsWithMirrorByBoxes(deck.sequenceLowerLeft))
                } else {
                    updateSeqLowerLeft(repository.getOriginsWithMirrorByBoxes(deck.sequenceLowerLeft))
                }
            }
            launch {
                if (SettingRepository.isUseCNEditionMod){
                    updateSeqLowerRight(repository.getCEsWithMirrorByBoxes(deck.sequenceLowerRight))
                } else {
                    updateSeqLowerRight(repository.getOriginsWithMirrorByBoxes(deck.sequenceLowerRight))
                }
            }
        }
    }

    /**给moveSelectFragment用的筛选类*/
    private val _filterOptionFlow :MutableStateFlow<FilterOption> = MutableStateFlow(FilterOption(AttackTowardOption.all(),
        AttackAltitudeOption.all(), AttackDirectionOption.all()))
    val filterOptionFlow = _filterOptionFlow.asStateFlow()
    fun changeFilter(filter :FilterOption){
        viewModelScope.launch(Dispatchers.IO){
            Log.i(TAG, "changeFilter: 发射filter")
            _filterOptionFlow.update { filter.copy() }// 注意，这里要发copy，不然stateFlow会看是同一个引用然后不更新
        }
    }
    fun initFilterOption(){
        viewModelScope.launch(Dispatchers.IO){
            _filterOptionFlow.update { FilterOption(AttackTowardOption.all(),
                AttackAltitudeOption.all(), AttackDirectionOption.all()) }
        }
    }

    private val _moveBeClickedFlow :MutableStateFlow<Int> = MutableStateFlow(value = 0)
    val moveBeClickFlow = _moveBeClickedFlow.asStateFlow()
    fun selectWhatMoveInSeq(whatMoveInSeq :Int = 0){
        viewModelScope.launch(Dispatchers.IO){
            _moveBeClickedFlow.emit(whatMoveInSeq)
        }
    }

    /**直接按id查招式，没镜像处理*/
    suspend fun getSeqMovesByIds(seq :List<Int>) :List<MoveOrigin?> {
        return repository.getOriginListByIds(seq)
    }
    suspend fun getOptMoveById(optId :Int) :MoveOrigin?{
        return repository.getOriginMoveById(optId)
    }
    suspend fun getSeqCEMovesByIds(seq :List<Int>) :List<MoveCE?> {
        return repository.getCEListByIds(seq)
    }
    suspend fun getOptCEMoveById(optId :Int) :MoveCE?{
        return repository.getCEMoveById(optId)
    }

    private val _sideLimitFlow :MutableStateFlow<SideLimit> = MutableStateFlow(value = SideLimit.noLimit())
    val sideLimitFlow = _sideLimitFlow.asStateFlow()
    fun updateSideLimit(sideLimit: SideLimit){
        viewModelScope.launch(Dispatchers.IO){
            _sideLimitFlow.emit(sideLimit)
        }
    }

    /**给moveRecycleFragment用的，传递选择的招式*/
    private val _moveForSelectFlow :MutableStateFlow<MoveSelectFragment.MoveMsgState> = MutableStateFlow(value = MoveSelectFragment.MoveMsgState.SelectNull())
    val moveForSelectFlow = _moveForSelectFlow.asStateFlow()
    fun selectMove(moveForSelect: MoveForSelect){
        viewModelScope.launch(Dispatchers.IO){
            _moveForSelectFlow.emit(MoveSelectFragment.MoveMsgState.SelectOne(moveForSelect = moveForSelect))
        }
    }
    fun selectNull(){
        viewModelScope.launch {
            _moveForSelectFlow.emit(MoveSelectFragment.MoveMsgState.SelectNull())
        }
    }
    fun initSelectMove(){
        viewModelScope.launch(Dispatchers.IO){
            _moveForSelectFlow.update { MoveSelectFragment.MoveMsgState.SelectNull(true) }
        }
    }

}

class DeckEditViewModelFactory(private val repository: DeckEditRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(DeckEditState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return DeckEditState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}