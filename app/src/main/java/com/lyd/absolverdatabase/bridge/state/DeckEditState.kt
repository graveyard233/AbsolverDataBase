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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

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

    suspend fun getSeqDetailFromDeck(deck: Deck) :String{
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val seq0 = scope.async { getSeqDetail(seqList = repository.getBoxesWithMirrorByBoxes(deck.sequenceUpperRight)) }
        val seq1 = scope.async { getSeqDetail(seqList = repository.getBoxesWithMirrorByBoxes(deck.sequenceUpperLeft)) }
        val seq2 = scope.async { getSeqDetail(seqList = repository.getBoxesWithMirrorByBoxes(deck.sequenceLowerLeft)) }
        val seq3 = scope.async { getSeqDetail(seqList = repository.getBoxesWithMirrorByBoxes(deck.sequenceLowerRight)) }
        val opt0 = scope.async { getOptDetail(opt = repository.getBoxWithMirrorByBox(deck.optionalUpperRight)) }
        val opt1 = scope.async { getOptDetail(opt = repository.getBoxWithMirrorByBox(deck.optionalUpperLeft)) }
        val opt2 = scope.async { getOptDetail(opt = repository.getBoxWithMirrorByBox(deck.optionalLowerLeft)) }
        val opt3 = scope.async { getOptDetail(opt = repository.getBoxWithMirrorByBox(deck.optionalLowerRight)) }
        return "# ↗:${seq0.await()}\n" +
                "# ↖:${seq1.await()}\n" +
                "# ↙:${seq2.await()}\n" +
                "# ↘:${seq3.await()}\n" +
                "# ↗:${opt0.await()}\n" +
                "# ↖:${opt1.await()}\n" +
                "# ↙:${opt2.await()}\n" +
                "# ↘:${opt3.await()}\n"
    }
    private fun getSeqDetail(seqList: List<MoveBox>) :String{
        return seqList.map {
            if (it.moveId < 0){
                "_"
            } else if (Locale.getDefault().toLanguageTag().startsWith("zh")){// 只要是中文
                it.move?.name ?: "_"
            } else {
                it.move?.name_en?.ifEmpty {
                    it.move?.name ?: "_"
                }
            }
        }.joinToString()
    }
    private fun getOptDetail(opt :MoveBox) :String{
        return opt.let {
            if (it.moveId < 0){
                "_"
            } else if (Locale.getDefault().toLanguageTag().startsWith("zh")){
                it.move!!.name
            } else {
                it.move!!.name_en.ifEmpty {
                    it.move!!.name
                }
            }
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
                optUpperRight.emit(repository.getBoxWithMirrorByBox(deck.optionalUpperRight))
            }
            launch {
                optUpperLeft.emit(repository.getBoxWithMirrorByBox(deck.optionalUpperLeft))
            }
            launch {
                optLowerLift.emit(repository.getBoxWithMirrorByBox(deck.optionalLowerLeft))
            }
            launch {
                optLowerRight.emit(repository.getBoxWithMirrorByBox(deck.optionalLowerRight))
            }
        }
    }

    fun updateAllSequence(deck: Deck){// 千万不要修改这个deck的属性
        viewModelScope.launch/*(Dispatchers.IO)*/{
            launch {
                updateSeqUpperRight(repository.getBoxesWithMirrorByBoxes(deck.sequenceUpperRight))
            }
            launch {
                updateSeqUpperLeft(repository.getBoxesWithMirrorByBoxes(deck.sequenceUpperLeft))
            }
            launch {
                updateSeqLowerLeft(repository.getBoxesWithMirrorByBoxes(deck.sequenceLowerLeft))
            }
            launch {
                updateSeqLowerRight(repository.getBoxesWithMirrorByBoxes(deck.sequenceLowerRight))
            }
        }
    }

    /**查看MoveBar上面的招式数据的流*/
    private val _watchMsgInBarFlow :MutableStateFlow<Int> = MutableStateFlow(-1)
    val watchMsgInBarFlow = _watchMsgInBarFlow.asStateFlow()
    fun watchWhatMsgInBar(whatMoveBeClicked :Int){
        viewModelScope.launch {
            _watchMsgInBarFlow.emit(whatMoveBeClicked)
        }
    }

    /**给moveSelectFragment用的筛选类*/
    private val _filterOptionFlow :MutableStateFlow<FilterOption> = MutableStateFlow(FilterOption(AttackTowardOption.all(),
        AttackAltitudeOption.all(), AttackDirectionOption.all(),
        strengthList = mutableListOf(true,false,true),
        rangeRange = FilterOption.defRange,
        effectSet = hashSetOf(MoveEffect.STOP.name,MoveEffect.DODGE_UP.name,MoveEffect.DODGE_LOW.name,MoveEffect.DODGE_SIDE.name,MoveEffect.BREAK_DEFENCES.name,MoveEffect.SUPER_ARMOR.name,
            MoveEffect.BLOCK_COUNTER.name,MoveEffect.DOUBLE_ATTACK.name,MoveEffect.TRIPLE_ATTACK.name,MoveEffect.MID_LINE.name,MoveEffect.MENTAL_BLOW.name,MoveEffect.NULL.name),
        startFrameRange = FilterOption.defStartF,
        phyWeaknessRange = FilterOption.defPhyWeakness,
        phyOutputRange = FilterOption.defPhyOutput,
        hitAdvRange = FilterOption.defHitAdv,
        defAdvRange = FilterOption.defDefAdv
    ))
    val filterOptionFlow = _filterOptionFlow.asStateFlow()
    fun changeFilter(filter :FilterOption){
        viewModelScope.launch(Dispatchers.IO){
            Log.i(TAG, "changeFilter: 发射filter")
            _filterOptionFlow.emit(
                FilterOption(
                    attackToward = filter.attackToward,
                    attackAltitude = filter.attackAltitude,
                    attackDirection = filter.attackDirection,
                    strengthList = MutableList(3){
                        when(it){
                            0 ->filter.strengthList[0]
                            1 ->filter.strengthList[1]
                            2 ->filter.strengthList[2]
                            else ->true
                        }
                    },
                    rangeRange = filter.rangeRange,
                    effectSet = HashSet<String>().apply { addAll(filter.effectSet) },
                    startFrameRange = filter.startFrameRange,
                    phyWeaknessRange = filter.phyWeaknessRange,
                    phyOutputRange = filter.phyOutputRange,
                    hitAdvRange = filter.hitAdvRange,
                    defAdvRange = filter.defAdvRange
                )
            ) //{}// 注意，这里要发copy，不然stateFlow会看是同一个引用然后不更新
            // 现在这里copy也可能接受不到，可能是list的原因，直接新建一个吧
        }
    }
    fun initFilterOption(){
        viewModelScope.launch(Dispatchers.IO){
            _filterOptionFlow.update { FilterOption(AttackTowardOption.all(),
                AttackAltitudeOption.all(), AttackDirectionOption.all(),
                strengthList = mutableListOf(true,true,true),
                rangeRange = FilterOption.defRange,
                effectSet = hashSetOf(MoveEffect.STOP.name,MoveEffect.DODGE_UP.name,MoveEffect.DODGE_LOW.name,MoveEffect.DODGE_SIDE.name,MoveEffect.BREAK_DEFENCES.name,MoveEffect.SUPER_ARMOR.name,
                    MoveEffect.BLOCK_COUNTER.name,MoveEffect.DOUBLE_ATTACK.name,MoveEffect.TRIPLE_ATTACK.name,MoveEffect.MID_LINE.name,MoveEffect.MENTAL_BLOW.name,MoveEffect.NULL.name),
                startFrameRange = FilterOption.defStartF,
                phyWeaknessRange = FilterOption.defPhyWeakness,
                phyOutputRange = FilterOption.defPhyOutput,
                hitAdvRange = FilterOption.defHitAdv,
                defAdvRange = FilterOption.defDefAdv
                )
            }
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
    suspend fun getSeqMovesByIds(seq :List<Int>) :List<Move?> {
        return repository.getMoveListByIds(seq)
    }
    suspend fun getOptMoveById(optId :Int) :Move?{
        return repository.getMoveById(optId)
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