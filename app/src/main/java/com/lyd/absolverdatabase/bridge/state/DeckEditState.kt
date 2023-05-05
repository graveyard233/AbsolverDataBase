package com.lyd.absolverdatabase.bridge.state

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.repository.DeckEditRepository
import com.lyd.absolverdatabase.ui.page.DeckEditFragment
import com.lyd.absolverdatabase.ui.page.MoveSelectFragment
import com.lyd.absolverdatabase.utils.DeckGenerate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**用于[DeckEditFragment]和[MoveSelectFragment]，在这里写的函数要标注好给哪个fragment用*/
class DeckEditState(private val repository: DeckEditRepository,
                    private val savedState: SavedStateHandle) :ViewModel()
{
    private val TAG = "${javaClass.simpleName}-${hashCode()}"

    init {
        savedState["deckInSaved"] = DeckGenerate.generateEmptyDeck()
    }
    /**
     * 状态是1的话，说明是从卡组列表界面点进来的
     * 状态是2的话，是从moveSelect中退回来的
     * */
    val editEventState = MutableStateFlow<Int>(1)

    fun getDeckInSaved() : Deck? = savedState.get<Deck>("deckInSaved")

    fun saveDeckInSaved(deck: Deck) = savedState.set("deckInSaved",deck)

    // 我可以用stateFlow来说明现在的状态，比如
    // 1，说明是从卡组列表界面点进来的
    // 2，从moveSelect中退回来的，这时候需要获取
    // 这里用saveState存一个deck，等保存的时候用，而上面的状态改变都应该触发这个deck的保存变更
    // 剩下的用没有缓存的sharedFlow来避免倒灌，只要这些sharedFlow变化就保存那个deck

    val sequenceUpperRight :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    val sequenceUpperLeft :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    val sequenceLowerLeft :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()
    val sequenceLowerRight :MutableSharedFlow<MutableList<Int>> = MutableSharedFlow()


    suspend fun getOriginListByIdsTest(idList: List<Int>):List<MoveOrigin>{
        val list = repository.getOriginListByIds(idList)
//        list.forEach {
//            Log.i(TAG, "getOriginListByIdsTest: $it")
//        }
        return list

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