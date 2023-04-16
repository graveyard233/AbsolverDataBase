package com.lyd.absolverdatabase.bridge.state

import android.graphics.Color
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.repository.DeckRepository

class DeckState(private val repository: DeckRepository,private val state : SavedStateHandle) : ViewModel() {

    val choiceFlow = state.getStateFlow("choiceWhat",0)

    fun setChoice(position :Int){
        state["choiceWhat"] = position
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