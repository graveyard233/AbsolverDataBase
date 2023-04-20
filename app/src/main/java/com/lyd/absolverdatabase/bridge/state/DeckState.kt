package com.lyd.absolverdatabase.bridge.state

import android.graphics.Color
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeckState(private val repository: DeckRepository,private val state : SavedStateHandle) : ViewModel() {

    val choiceFlow = state.getStateFlow("choiceWhat",0)

    fun setChoice(position :Int){
        state["choiceWhat"] = position
    }

    val deckSharedFlow :MutableSharedFlow<List<Deck>> = MutableSharedFlow<List<Deck>>()
    suspend fun queryDecksByDeckType(
        deckType: DeckType,
        ifEmpty: () ->Any? = {  },
        ifError: (errorMsg: String) -> Any? = {  }
    ){
        viewModelScope.launch{
            repository.queryDecksByDeckType(deckType).collectLatest {
                when(it){
                    is RpEmpty -> {
                        ifEmpty.invoke()
                    }
                    is RpError -> {
                        ifError.invoke(it.error)
                    }
                    is RpSuccess -> {
                        deckSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }
}

class DeckViewModelFactory(private val repository: DeckRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(DeckState::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return DeckState(repository,savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}