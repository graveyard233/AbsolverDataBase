package com.lyd.absolverdatabase.bridge.state

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.DeckEditRepository
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
    fun saveDeckInSaved(deck: Deck) {
        savedState.set("deckInSaved", deck)
        // 保存的时候应该还要触发序列sharedFlow的变化
    }

    val deckInSaved = savedState.getStateFlow("deckInSaved",DeckGenerate.generateEmptyDeck())
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 0
        )





    val optUpperRight :MutableSharedFlow<Int> = MutableSharedFlow()
    val optUpperLeft :MutableSharedFlow<Int> = MutableSharedFlow()
    val optLowerLift :MutableSharedFlow<Int> = MutableSharedFlow()
    val optLowerRight :MutableSharedFlow<Int> = MutableSharedFlow()

    private val _sequenceUpperRight :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    private val _sequenceUpperLeft :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    private val _sequenceLowerLeft :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    private val _sequenceLowerRight :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()

    val sequenceUpperRight = _sequenceUpperRight.asSharedFlow()
    val sequenceUpperLeft  = _sequenceUpperLeft .asSharedFlow()
    val sequenceLowerLeft  = _sequenceLowerLeft .asSharedFlow()
    val sequenceLowerRight = _sequenceLowerRight.asSharedFlow()

    suspend fun updateSeqUpperRight(list :MutableList<Int>) = _sequenceUpperRight.emit(list)
    suspend fun updateSeqUpperLeft(list :MutableList<Int>) = _sequenceUpperLeft.emit(list)
    suspend fun updateSeqLowerLeft(list :MutableList<Int>) = _sequenceLowerLeft.emit(list)
    suspend fun updateSeqLowerRight(list :MutableList<Int>) = _sequenceLowerRight.emit(list)

    fun updateAllOption(deck: Deck){
        viewModelScope.launch(Dispatchers.IO){
            launch {
                optUpperRight.emit(deck.optionalUpperRight)
            }
            launch {
                optUpperLeft.emit(deck.optionalUpperLeft)
            }
            launch {
                optLowerLift.emit(deck.optionalLowerLeft)
            }
            launch {
                optLowerRight.emit(deck.optionalLowerRight)
            }
        }
    }

    fun updateAllSequence(deck: Deck){// 千万不要修改这个deck的属性
        viewModelScope.launch/*(Dispatchers.IO)*/{
            launch {
                updateSeqUpperRight(deck.sequenceUpperRight)
            }
            launch {
                updateSeqUpperLeft(deck.sequenceUpperLeft)
            }
            launch {
                updateSeqLowerLeft(deck.sequenceLowerLeft)
            }
            launch {
                updateSeqLowerRight(deck.sequenceLowerRight)
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

    suspend fun getSeqMovesByIds(seq :List<Int>) :List<MoveOrigin?> {
        return repository.getOriginListByIds(seq)
    }
    suspend fun getOptMoveById(optId :Int) :MoveOrigin?{
        return repository.getOriginMoveById(optId)
    }

    private val _sideLimitFlow :MutableStateFlow<SideLimit> = MutableStateFlow(value = SideLimit.noLimit())
    val sideLimitFlow = _sideLimitFlow.asStateFlow()
    fun updateSideLimit(sideLimit: SideLimit){
        viewModelScope.launch(Dispatchers.IO){
            _sideLimitFlow.emit(sideLimit)
        }
    }

    /**给moveRecycleFragment用的，传递选择的招式*/
    private val _moveForSelectFlow :MutableStateFlow<MoveSelectFragment.MoveMsgState> = MutableStateFlow(value = MoveSelectFragment.MoveMsgState.SelectNull)
    val moveForSelectFlow = _moveForSelectFlow.asStateFlow()
    fun selectMove(moveForSelect: MoveForSelect){
        viewModelScope.launch(Dispatchers.IO){
            _moveForSelectFlow.emit(MoveSelectFragment.MoveMsgState.SelectOne(moveForSelect = moveForSelect))
        }
    }
    fun initSelectMove(){
        viewModelScope.launch(Dispatchers.IO){
            _moveForSelectFlow.update { MoveSelectFragment.MoveMsgState.SelectNull }
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