package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentMoveSelectBinding
import com.lyd.absolverdatabase.ui.adapter.MovePagerAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MoveSelectFragment :BaseFragment(){

    sealed class MoveMsgState{
        data class SelectOne(val moveForSelect: MoveForSelect) :MoveMsgState()
        object SelectNull : MoveMsgState()
    }

    private val editState :DeckEditState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckEditViewModelFactory((mActivity?.application as App).deckEditRepository)
    })

    private val argMsg :MoveSelectFragmentArgs by navArgs()

    private var dataBinding : FragmentMoveSelectBinding ?= null

    private var barLazy :View ?=null

    private val spinnerTowardAdapter :ArrayAdapter<String> by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,AttackTowardOption.getOptionStr())
    }
    private val spinnerAltitudeAdapter :ArrayAdapter<String> by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,AttackAltitudeOption.getOptionStr())
    }
    private val spinnerDirectionAdapter :ArrayAdapter<String> by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        ArrayAdapter<String>(requireContext(),com.google.android.material.R.layout.support_simple_spinner_dropdown_item,AttackDirectionOption.getOptionStr())
    }

    private val filterOption :FilterOption by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        FilterOption(attackToward = AttackTowardOption.all(), attackAltitude = AttackAltitudeOption.all(), attackDirection = AttackDirectionOption.all())
    }
    private var launchFilterTime :Long = 0L

    private val movePagerAdapter :MovePagerAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MovePagerAdapter(this@MoveSelectFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_move_select,container,false)

        dataBinding = FragmentMoveSelectBinding.bind(view)
        dataBinding?.lifecycleOwner = viewLifecycleOwner
        dataBinding?.vm = editState

        when (argMsg.toSelectMsg.whatBarToEdit){
            in 0..3 ->{// 应该加载带3个按钮的MovesBar
                dataBinding?.moveSelectViewStub?.viewStub?.layoutResource = R.layout.bar_moves
//                dataBinding?.guidelineBarBottom?.setGuidelinePercent(0.35F)
            }
            in 4..7 ->{// 应该加载oneMoveBar
                dataBinding?.moveSelectViewStub?.viewStub?.layoutResource = R.layout.bar_one_move
//                dataBinding?.guidelineBarBottom?.setGuidelinePercent(0.4F)
            }
        }

        dataBinding?.moveSelectViewStub?.viewStub?.inflate()
        // viewStub加载完成之后，要在onViewCreate那里才能找到加载的view
        dataBinding?.apply {
            moveSelectPager?.adapter = movePagerAdapter
            val iconList = listOf<Int>(R.drawable.ic_upper_right_bold,R.drawable.ic_upper_left_bold,
            R.drawable.ic_lower_left_bold,R.drawable.ic_lower_right_bold)
            if (moveSelectTab != null) {
                if (moveSelectPager != null) {
                    TabLayoutMediator(moveSelectTab,moveSelectPager){tab, position ->
                        tab.setIcon(iconList[position])
                    }.attach()
                }
            }
            moveSelectSpinnerToward?.apply{
                adapter = spinnerTowardAdapter
                onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {// 当选择和上次选的一样就不会触发这个回调
                        Log.i(TAG, "on attackToward Selected: ${spinnerTowardAdapter.getItem(p2)}")
                        filterOption.attackToward = AttackTowardOption.getOptions()[p2]
                        doAfterChangeFilterManual()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
//                        TODO("Not yet implemented")
                    }
                }
            }
            moveSelectSpinnerAltitude?.apply {
                adapter = spinnerAltitudeAdapter
                onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        Log.i(TAG, "on attackAltitude Selected: ${spinnerAltitudeAdapter.getItem(p2)}")
                        filterOption.attackAltitude = AttackAltitudeOption.getOptions()[p2]
                        doAfterChangeFilterManual()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
//                        TODO("Not yet implemented")
                    }
                }
            }
            moveSelectSpinnerDirection?.apply {
                adapter = spinnerDirectionAdapter
                onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        Log.i(TAG, "on attackDirection Selected: ${spinnerDirectionAdapter.getItem(p2)}")
                        filterOption.attackDirection = AttackDirectionOption.getOptions()[p2]
                        doAfterChangeFilterManual()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
//                        TODO("Not yet implemented")
                    }
                }
            }

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        barLazy = requireView().findViewById(R.id.moveSelect_bar)
        // 在这里可以找到加载布局的控件

//        try {
//            barLazy = requireView().findViewById(R.id.moveSelect_bar)
////            val tempBar :MovesBar = barLazy as MovesBar // 做不到强制转换，因为这不是一个view，而是一个layout，所以要靠
//            val tempImg = barLazy?.findViewById<ImageView>(R.id.bar_move_0)
//            tempImg!!.setImageResource(R.drawable.ic_faejin)
//        } catch (e :Exception){
//            Log.e(TAG, "onCreateView: ", e)
//        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.filterOptionFlow.collectLatest {
                    if (filterOption.changeBy.get() == 0){
                        Log.i(TAG, "filterOptionFlow: 非手动，从其他界面进来的，应该变更spinner布局")
                        filterOption.changeAll(it)
                        tryChangeSpinner(it)
                    } else if (filterOption.changeBy.get() == 1) {
                        Log.i(TAG, "filterOptionFlow : 是手动改变的，不是其他界面切回来的，不用变更布局")
                        filterOption.changeBy.set(0)
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.moveForSelectFlow.collectLatest {
                    when(it){
                        is MoveMsgState.SelectNull -> { removeMsg() }
                        is MoveMsgState.SelectOne -> {
                            setMoveMsg(it.moveForSelect)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataBinding = null
    }

    private fun setMoveMsg(moveForSelect: MoveForSelect) {
        val tempMove = moveForSelect.moveOrigin
        dataBinding?.moveSelectInclude?.apply {
            // 设置第一部分
            GlideApp.with(msgImg)
                .load(AssetsUtil.getBitmapByMoveId(requireContext(), moveId = tempMove.id))
                .into(msgImg)

            // 设置第二部分
            GlideApp.with(msgStartSideImg)
                .load(SideUtil.imgIdForMoves(tempMove.startSide))
                .into(msgStartSideImg)
            GlideApp.with(msgEndSideImg)
                .load(SideUtil.imgIdForMoves(tempMove.endSide))
                .into(msgEndSideImg)
            msgName.text = tempMove.name

            msgStrength.text = getString(
                R.string.moveMsg_strength,
                when (tempMove.strength) {
                    1 -> "轻"
                    2 -> "中"
                    3 -> "重"
                    else -> "error"
                }
            )
            msgRange.text = getString(R.string.moveMsg_range, tempMove.attackRange)
            "${
                when (tempMove.attackToward) {
                    AttackToward.LEFT -> "左"
                    AttackToward.RIGHT -> "右"
                }
            }${
                when (tempMove.attackAltitude) {
                    AttackAltitude.LOW -> "低"
                    AttackAltitude.MIDDLE -> "中"
                    AttackAltitude.HEIGHT -> "高"
                }
            }位${
                when (tempMove.attackDirection) {
                    AttackDirection.HORIZONTAL -> "横向"
                    AttackDirection.VERTICAL -> "纵向"
                    AttackDirection.POKE -> "戳击"
                }
            }".let {
                msgAttackTowardDetail.text = it
            }
            val sb = StringBuilder()
            tempMove.effect.split(",").apply {
                onEachIndexed { index, effectStr ->
                    sb.append(when(effectStr){
                        MoveEffect.STOP.name -> MoveEffect.STOP.str
                        MoveEffect.DODGE_UP.name -> MoveEffect.DODGE_UP.str
                        MoveEffect.DODGE_LOW.name -> MoveEffect.DODGE_LOW.str
                        MoveEffect.DODGE_SIDE.name -> MoveEffect.DODGE_SIDE.str
                        MoveEffect.BREAK_DEFENCES.name -> MoveEffect.BREAK_DEFENCES.str
                        MoveEffect.SUPER_ARMOR.name -> MoveEffect.SUPER_ARMOR.str
                        MoveEffect.BLOCK_COUNTER.name -> MoveEffect.BLOCK_COUNTER.str
                        MoveEffect.DOUBLE_ATTACK.name -> MoveEffect.DOUBLE_ATTACK.str
                        MoveEffect.MID_LINE.name -> MoveEffect.MID_LINE.str
                        MoveEffect.MENTAL_BLOW.name -> MoveEffect.MENTAL_BLOW.str
                        MoveEffect.NULL.name -> MoveEffect.NULL.str
                        else -> { "error" }
                    })
                    if (this.size == 2){
                        if (index == 0)
                            sb.append(",")
                    } else if (this.size == 3){
                        if (index == 0 || index == 1){
                            sb.append(",")
                        }
                    }
                }
            }
            msgEffect.text = getString(R.string.moveMsg_effect,sb.toString())

            // 设置第三部分
            msgStartFrame.text = getString(R.string.moveMsg_startFrame,tempMove.startFrame)
            msgPhyOutput.text = getString(R.string.moveMsg_phyOutput,tempMove.physicalOutput)
            msgPhyWeakness.text = getString(R.string.moveMsg_phyWeakness,tempMove.physicalWeakness)
            msgHitAdvantage.text = getString(R.string.moveMsg_hitAdvantage,tempMove.hitAdvantageFrame)
            msgDefenseAdvantage.text = getString(R.string.moveMsg_defenseAdvantage,tempMove.defenseAdvantageFrame)
        }
    }
    private fun removeMsg(){
        dataBinding?.moveSelectInclude?.apply {
            val textHolder = null
            // 移除第一部分
            GlideApp.with(msgImg).clear(msgImg)

            // 移除第二部分
            GlideApp.with(msgStartSideImg).clear(msgStartSideImg)
            GlideApp.with(msgEndSideImg).clear(msgEndSideImg)
            msgName.text = textHolder

            msgStrength.text = textHolder
            msgRange.text = textHolder
            msgAttackTowardDetail.text = textHolder
            msgEffect.text = textHolder

            // 设置第三部分
            msgStartFrame.text = textHolder
            msgPhyOutput.text = textHolder
            msgPhyWeakness.text = textHolder
            msgHitAdvantage.text = textHolder
            msgDefenseAdvantage.text = textHolder
        }
    }

    private fun doAfterChangeFilterManual (){// 应该在发射这里进行拦截，确保collect的时候总会更新，然后recycle那边也不会收到很多次
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - launchFilterTime) < 0.8 && launchFilterTime != 0L){
            Log.i(TAG, "doAfterChangeFilterManual: 过于频繁，放弃")
        } else {
            launchFilterTime = System.currentTimeMillis();
            editState.changeFilter(filterOption.apply { changeBy.set(1) })
        }

    }

    private fun tryChangeSpinner(filter :FilterOption){
        dataBinding?.apply {
            moveSelectSpinnerToward?.apply {
                if (AttackTowardOption.getOptions()[selectedItemPosition] != filter.attackToward){
                    // 不同就要更新界面
                    Log.i(TAG, "tryChangeSpinner: attackToward 不一样，更新")
                    setSelection(filterOption.attackToward.num)
                }
            }
            moveSelectSpinnerAltitude?.apply {
                if (AttackAltitudeOption.getOptions()[selectedItemPosition] != filter.attackAltitude){
                    // 不同就要更新界面
                    Log.i(TAG, "tryChangeSpinner: attackAltitude 不一样，更新")
                    setSelection(filterOption.attackAltitude.num)
                }
            }
            moveSelectSpinnerDirection?.apply {
                if (AttackDirectionOption.getOptions()[selectedItemPosition] != filter.attackDirection){
                    // 不同就要更新界面
                    Log.i(TAG, "tryChangeSpinner: attackDirection 不一样，更新")
                    setSelection(filterOption.attackDirection.num)
                }
            }
        }
    }

}