package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.bridge.state.MoveRecycleState
import com.lyd.absolverdatabase.bridge.state.MoveRecycleViewModelFactory
import com.lyd.absolverdatabase.ui.adapter.MoveItemAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.SideUtil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 注意，这里不要在构造函数里面放参数，除非把baseFragment的构造函数变成open，要什么东西用bundle传递就好
 * */
class MoveRecycleFragment :BaseFragment()
{

    private var whatEndSide :Int = 0

    // 由于创建了很多个Fragment，所以这个也不是唯一的
    private val moveRecycleState by viewModels<MoveRecycleState> {
        MoveRecycleViewModelFactory((mActivity?.application as App).moveRepository)
    }

    private val _filter :FilterOption by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        FilterOption(attackToward = AttackTowardOption.all(), attackAltitude = AttackAltitudeOption.all(), attackDirection = AttackDirectionOption.all())
    }

    // 这个是唯一的，所以通过这个来设置筛选的选项，所以这个state用来观察筛选flow
    private val editState : DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })


    private val moveAdapter :MoveItemAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MoveItemAdapter().apply {
            setOnItemClickListener(object :BaseQuickAdapter.OnItemClickListener<MoveForSelect>{
                override fun onClick(
                    adapter: BaseQuickAdapter<MoveForSelect, *>,
                    view: View,
                    position: Int
                ) {
//                    Log.i(TAG, "onClick: 我点击的招式是 ${adapter.getItem(position)!!.moveOrigin}")
                    adapter.getItem(position)?.run {
                        editState.selectMove(this)
                    }
                }
            })
        }
    }

    private val gridLayoutManager :GridLayoutManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        GridLayoutManager(requireContext(),4)
    }

    private val _sideList = mutableListOf<MoveForSelect>()

    private lateinit var recycle :RecyclerView

    private var isFirstEnter = true

    @Volatile
    private var _limit :SideLimit = SideLimit.noLimit()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_move_recycle,container,false)

        arguments?.getInt("whatEndSide")?.let {
            whatEndSide = it
        }

        recycle = view.findViewById(R.id.moveRecycle_recycle)

        recycle.apply {
            layoutManager = gridLayoutManager
        }.apply {
            adapter = moveAdapter
        }

        llog.i(TAG, "onCreateView: side ${SideUtil.getSideByInt(whatEndSide)}")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.sideLimitFlow.collectLatest { it ->// 在这里要改变_limit和_sideList
                    llog.i(TAG, "side->${SideUtil.getSideByInt(whatEndSide)}: 接受到招式限制 $it")
                    // 变化玩limit之后，需要实现实时筛选对应起始站架的招式
                    synchronized(_limit){
                        _limit = it
                    }
                    // 得到根据起始结束站架限制筛选后的招式列表后，还需要按另外一个筛选项->filterOption 来进行最后的筛选
                    val resultBySide = filterBySideLimit(sideLimit = it)
                    synchronized(_sideList){
                        _sideList.clear()
                        _sideList.addAll(resultBySide)
                    }
                    val resultList = filterByOpt(_sideList,_filter)
                    moveAdapter.submitList(resultList)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            // TODO: 准备改成结束在，别人依靠的是在editFragment或者其他数据拿到起始占位，然后这里找的是结束站位的招式
            // TODO: 现在的问题是我也需要按起始站架来筛选招式，tab只是限制了结束站架，然而选择的框是限制起始站架
            // TODO: 招式是可以镜像的（只适用于徒手卡组，剑卡除外），以直拳为例，可以右上->左上,也可以左上->右上，然后左右也是有镜像的，同样是根据是否启用镜像来确认是否反转显示的
            // 又因为我要根据每个序列的招式进行筛选，起始站架和结束站架都是不一样的，所以我需要在这里持有徒手或剑卡的所有数据，然后在这里进行筛选
            // whatSide 还是当成结束站架
            // 外面会传入一个flow，是起始站架，然后根据这个进行筛选招式
            // TODO: 首先判断是不是找剑卡，假如不是，那就是找徒手卡组
            // TODO: 如果是徒手卡组，则判断起始站架，寻找所有和原本站架和镜像站架相关的招式，找到之后将不和原本站架相同的招式的结束站架和左右全部转成镜像的数据
            // TODO: 最后再根据结束站架分发list
            editState.filterOptionFlow.collectLatest {// 在这里要改变_filter，按照_sideList来筛选输出
                llog.i(TAG, "side->${SideUtil.getSideByInt(whatEndSide)} 接受到筛选数据: Toward->${it.attackToward.name}" +
                        " Altitude->${it.attackAltitude.name} Direction->${it.attackDirection.name}")
                if (_filter.isFilterSame(it)){
                    // 一样的数据，不用变动
                    llog.i(TAG, "side->${SideUtil.getSideByInt(whatEndSide)} editState.filterOptionFlow: 数据和内部的一样，不需要动_filter")
                    if (isFirstEnter){
                        llog.i(TAG, "filterOptionFlow: isFirst->$isFirstEnter 第一次进来，还是要获取数据")
                        isFirstEnter = false
                        val resultList = filterByOpt(_sideList,_filter)
                        moveAdapter.submitList(resultList)
                    }
                } else {
                    // 不一样，要重新筛选
                    llog.i(TAG, "side->${SideUtil.getSideByInt(whatEndSide)} editState.filterOptionFlow: 不一样，_filter要重新设置")
                    synchronized(_filter){
                        _filter.changeAll(it)
                    }
                    // 根据sideList来进行筛选
                    val resultList = filterByOpt(_sideList,_filter)
                    resultList.filter { select ->
                        select.isMirror == 1
                    }.forEach {temp->
                        llog.i(TAG, "list中镜像的招式有: $temp")
                    }
                    moveAdapter.submitList(resultList)
//                    filterList(_sideList,_filter)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        llog.i(TAG, "onDestroyView:  side ${SideUtil.getSideByInt(whatEndSide)}")
    }


    /**先执行这个，按站架筛选，筛出来的结果再交给[filterByOpt]*/
    private suspend fun filterBySideLimit(sideLimit: SideLimit,canHand :Boolean = true) :List<MoveForSelect>{
        return sideLimit.let {
            val tempCanHand = when(editState.getDeckInSaved()?.deckType){
                DeckType.HAND -> true
                DeckType.GLOVE -> true
                DeckType.SWORD -> false
                null -> true
            }
            withContext(Dispatchers.IO) {
                val tempMoveListBeSelect = async {
                    val tempList = mutableListOf<Int>()
                    val tempDeck = editState.getDeckInSaved()
                    tempDeck?.sequenceUpperRight?.forEach { tempBox ->
                        if (tempBox.moveId != -1){
                            tempList.add(tempBox.moveId)
                        }
                    }
                    tempDeck?.sequenceUpperLeft?.forEach { tempBox ->
                        if (tempBox.moveId != -1){
                            tempList.add(tempBox.moveId)
                        }
                    }
                    tempDeck?.sequenceLowerLeft?.forEach { tempBox ->
                        if (tempBox.moveId != -1){
                            tempList.add(tempBox.moveId)
                        }
                    }
                    tempDeck?.sequenceLowerRight?.forEach { tempBox ->
                        if (tempBox.moveId != -1){
                            tempList.add(tempBox.moveId)
                        }
                    }
                    tempDeck?.optionalUpperRight?.moveId?.let { optId ->
                        if (optId != -1){
                            tempList.add(optId)
                        }
                    }
                    tempDeck?.optionalUpperLeft?.moveId?.let { optId ->
                        if (optId != -1){
                            tempList.add(optId)
                        }
                    }
                    tempDeck?.optionalLowerLeft?.moveId?.let { optId ->
                        if (optId != -1){
                            tempList.add(optId)
                        }
                    }
                    tempDeck?.optionalLowerRight?.moveId?.let { optId ->
                        if (optId != -1){
                            tempList.add(optId)
                        }
                    }
                    tempList
                }
                val tempForSelect = when(it){
                    is SideLimit.noLimit -> {
                        llog.i(TAG, "noLimit: ${it.msg}")
                        moveRecycleState.moveListWithMirror(null,whatEndSide,tempCanHand)
                    }
                    is SideLimit.limitAll -> {
                        llog.i(TAG, "limitAll: start:${it.startSide} end:${it.endSide}")
                        moveRecycleState.moveListWithMirror(SideUtil.getIntBySide(it.startSide),SideUtil.getIntBySide(it.endSide),tempCanHand)
                    }
                    is SideLimit.limitStart -> {
                        llog.i(TAG, "limitStart: start:${it.startSide}")
                        moveRecycleState.moveListWithMirror(SideUtil.getIntBySide(it.startSide),whatEndSide,tempCanHand)
                    }
                    is SideLimit.limitEnd -> {
                        llog.i(TAG, "limitEnd: end:${it.endSide}")
                        moveRecycleState.moveListWithMirror(null, endInt = whatEndSide,tempCanHand)

                    }
                    is SideLimit.optLimit -> {
                        llog.w(TAG, "optLimit: ${it.startSide}")
                        // TODO: 2023/6/4 这里要做一个专门给自选序列一个专门的筛选方法
                        // TODO: 2023/6/9 这个数据还有点问题
                        moveRecycleState.optListWithMirror(SideUtil.getIntBySide(it.startSide),whatEndSide,tempCanHand)
                    }
                }
                val idsBeSelect = tempMoveListBeSelect.await()
                if (SettingRepository.isUseCNEditionMod){
                    tempForSelect.forEach { tempMoveForSelect->
                        if (tempMoveForSelect.moveCE.id in idsBeSelect){
                            tempMoveForSelect.isSelected = true// 标记已选择的招式
                        }
                    }
                } else {
                    tempForSelect.forEach { tempMoveForSelect->
                        if (tempMoveForSelect.moveOrigin.id in idsBeSelect){
                            tempMoveForSelect.isSelected = true// 标记已选择的招式
                        }
                    }
                }

                tempForSelect
            }
        }
    }
    /**执行完[filterBySideLimit]后再执行这个，按选项筛选，输出[MoveForSelect]的list，以便标记招式，所以筛选都按这个流程走*/
    private suspend fun filterByOpt(sideList: MutableList<MoveForSelect>, option: FilterOption) :List<MoveForSelect>{
        return withContext(Dispatchers.Default){
            if (SettingRepository.isUseCNEditionMod){
                sideList.filter {
                    when(option.attackToward){
                        is AttackTowardOption.left -> { it.moveCE.attackToward == AttackToward.LEFT }
                        is AttackTowardOption.right -> { it.moveCE.attackToward == AttackToward.RIGHT }
                        is AttackTowardOption.all -> true
                    }
                }.filter {
                    when(option.attackAltitude){
                        is AttackAltitudeOption.height -> { it.moveCE.attackAltitude == AttackAltitude.HEIGHT }
                        is AttackAltitudeOption.middle -> { it.moveCE.attackAltitude == AttackAltitude.MIDDLE }
                        is AttackAltitudeOption.low -> { it.moveCE.attackAltitude == AttackAltitude.LOW }
                        is AttackAltitudeOption.all -> true
                    }
                }.filter {
                    when(option.attackDirection){
                        is AttackDirectionOption.horizontal -> { it.moveCE.attackDirection == AttackDirection.HORIZONTAL }
                        is AttackDirectionOption.vertical -> { it.moveCE.attackDirection == AttackDirection.VERTICAL }
                        is AttackDirectionOption.poke -> { it.moveCE.attackDirection == AttackDirection.POKE }
                        is AttackDirectionOption.all -> true
                    }
                }
            } else {
                sideList.filter {
                    when(option.attackToward){
                        is AttackTowardOption.left -> { it.moveOrigin.attackToward == AttackToward.LEFT }
                        is AttackTowardOption.right -> { it.moveOrigin.attackToward == AttackToward.RIGHT }
                        is AttackTowardOption.all -> true
                    }
                }.filter {
                    when(option.attackAltitude){
                        is AttackAltitudeOption.height -> { it.moveOrigin.attackAltitude == AttackAltitude.HEIGHT }
                        is AttackAltitudeOption.middle -> { it.moveOrigin.attackAltitude == AttackAltitude.MIDDLE }
                        is AttackAltitudeOption.low -> { it.moveOrigin.attackAltitude == AttackAltitude.LOW }
                        is AttackAltitudeOption.all -> true
                    }
                }.filter {
                    when(option.attackDirection){
                        is AttackDirectionOption.horizontal -> { it.moveOrigin.attackDirection == AttackDirection.HORIZONTAL }
                        is AttackDirectionOption.vertical -> { it.moveOrigin.attackDirection == AttackDirection.VERTICAL }
                        is AttackDirectionOption.poke -> { it.moveOrigin.attackDirection == AttackDirection.POKE }
                        is AttackDirectionOption.all -> true
                    }
                }
            }
        }
    }

}