package com.lyd.absolverdatabase.bridge.state

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.AttackTowardOption
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.FilterOption
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.repository.DeckEditRepository
import com.lyd.absolverdatabase.ui.page.DeckEditFragment
import com.lyd.absolverdatabase.ui.page.MoveSelectFragment
import com.lyd.absolverdatabase.utils.DeckGenerate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    suspend fun getOriginListByIdsTest(idList: List<Int>):List<MoveOrigin>{
        val list = repository.getOriginListByIds(idList)
//        list.forEach {
//            Log.i(TAG, "getOriginListByIdsTest: $it")
//        }
        return list

    }


    /**给moveSelectFragment用的筛选类*/
    private val _filterOptionFlow :MutableSharedFlow<FilterOption> = MutableSharedFlow(replay = 1)
    val filterOptionFlow = _filterOptionFlow.asSharedFlow()

    fun initFilterOption(){
        viewModelScope.launch(Dispatchers.IO){
            _filterOptionFlow.emit(FilterOption(AttackTowardOption.all()))
        }
    }
    fun changeFilter(){
        viewModelScope.launch(Dispatchers.IO){
            _filterOptionFlow.emit(FilterOption(AttackTowardOption.getRandomOption()))
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