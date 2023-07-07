package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.DeckEditState
import com.lyd.absolverdatabase.bridge.state.DeckEditViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentMoveSelectBinding
import com.lyd.absolverdatabase.ui.adapter.MovePagerAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.absolverdatabase.utils.MoveGenerate
import com.lyd.absolverdatabase.utils.SideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MoveSelectFragment :BaseFragment(){

    sealed class MoveMsgState{
        data class SelectOne(val moveForSelect: MoveForSelect) :MoveMsgState()
        class SelectNull(val isEnterFromEdit:Boolean = false) : MoveMsgState()// 这里不要用data class 因为
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

    // 采用包的形式，一起打包招式和id，用起来更加方便，使用update方法，同时更新招式和id字段，也更加便利
    private var seqPack :SeqPack ?= null
    private var optionPack :OptPack ?= null

    private var sideStart :ImageView ?= null// 公共使用的side
    private var sideEnd :ImageView ?= null

    private var side1 :ImageView ?= null
    private var side2 :ImageView ?= null

    private var move0 :ShapeableImageView ?= null// 公共img
    private var move1 :ShapeableImageView ?= null
    private var move2 :ShapeableImageView ?= null
    private val moveImgList :MutableList<ShapeableImageView?> = mutableListOf<ShapeableImageView?>()


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
                lifecycleScope.launch(Dispatchers.IO){
                    // TODO: 这里获取的招式序列应该按镜像list来处理
                    seqPack = when(argMsg.toSelectMsg.whatBarToEdit){
                        0 ->{
                            SeqPack(startSide = StandSide.UPPER_RIGHT, isMirrorList = MoveBox.getMirrorList(editState.getDeckInSaved()!!.sequenceUpperRight)).apply {
                                if (SettingRepository.isUseCNEditionMod) {
                                    replaceCEList(editState.getSeqCEMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceUpperRight)))
                                } else {
                                    replaceList(editState.getSeqMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceUpperRight)))
                                }
                            }
                        }
                        1 ->{
                            SeqPack(startSide = StandSide.UPPER_LEFT, isMirrorList = MoveBox.getMirrorList(editState.getDeckInSaved()!!.sequenceUpperLeft)).apply {
                                if (SettingRepository.isUseCNEditionMod) {
                                    replaceCEList(editState.getSeqCEMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceUpperLeft)))
                                } else {
                                    replaceList(editState.getSeqMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceUpperLeft)))
                                }
                            }
                        }
                        2 ->{
                            SeqPack(startSide = StandSide.LOWER_LEFT, isMirrorList = MoveBox.getMirrorList(editState.getDeckInSaved()!!.sequenceLowerLeft)).apply {
                                if (SettingRepository.isUseCNEditionMod) {
                                    replaceCEList(editState.getSeqCEMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceLowerLeft)))
                                } else {
                                    replaceList(editState.getSeqMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceLowerLeft)))
                                }
                            }
                        }
                        3 ->{
                            SeqPack(startSide = StandSide.LOWER_RIGHT, isMirrorList = MoveBox.getMirrorList(editState.getDeckInSaved()!!.sequenceLowerRight)).apply {
                                if (SettingRepository.isUseCNEditionMod) {
                                    replaceCEList(editState.getSeqCEMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceLowerRight)))
                                } else {
                                    replaceList(editState.getSeqMovesByIds(MoveBox.getIdList(editState.getDeckInSaved()!!.sequenceLowerRight)))
                                }
                            }
                        }
                        else ->{
                            SeqPack(startSide = StandSide.UPPER_RIGHT).apply {
                                if (SettingRepository.isUseCNEditionMod) {
                                    replaceCEList(editState.getSeqCEMovesByIds(listOf<Int>(-1,-1,-1)))
                                } else {
                                    replaceList(editState.getSeqMovesByIds(listOf<Int>(-1,-1,-1)))
                                }
                            }
                        }
                    }
                    whenClickMoveInBar(editState.moveBeClickFlow.value)
                }

            }
            in 4..7 ->{// 应该加载oneMoveBar
                dataBinding?.moveSelectViewStub?.viewStub?.layoutResource = R.layout.bar_one_move
//                dataBinding?.guidelineBarBottom?.setGuidelinePercent(0.4F)
                lifecycleScope.launch(Dispatchers.IO){
                    optionPack = OptPack(startSide = when(argMsg.toSelectMsg.whatBarToEdit){
                        4 -> StandSide.UPPER_RIGHT
                        5 -> StandSide.UPPER_LEFT
                        6 -> StandSide.LOWER_LEFT
                        7 -> StandSide.LOWER_RIGHT
                        else -> StandSide.UPPER_RIGHT
                    }).apply {
                        if (SettingRepository.isUseCNEditionMod){
                            updateOpt(
                                moveCE = editState.getOptCEMoveById(
                                    when (argMsg.toSelectMsg.whatBarToEdit) {
                                        4 -> editState.getDeckInSaved()!!.optionalUpperRight.moveId
                                        5 -> editState.getDeckInSaved()!!.optionalUpperLeft.moveId
                                        6 -> editState.getDeckInSaved()!!.optionalLowerLeft.moveId
                                        7 -> editState.getDeckInSaved()!!.optionalLowerRight.moveId
                                        else -> -1
                                    }
                                ),
                                isUseMirror = when (argMsg.toSelectMsg.whatBarToEdit) {
                                    4 -> editState.getDeckInSaved()!!.optionalUpperRight.isUseMirror
                                    5 -> editState.getDeckInSaved()!!.optionalUpperLeft.isUseMirror
                                    6 -> editState.getDeckInSaved()!!.optionalLowerLeft.isUseMirror
                                    7 -> editState.getDeckInSaved()!!.optionalLowerRight.isUseMirror
                                    else -> 0
                                }
                            )
                        } else {
                            updateOpt(
                                moveOrigin = editState.getOptMoveById(
                                    when (argMsg.toSelectMsg.whatBarToEdit) {
                                        4 -> editState.getDeckInSaved()!!.optionalUpperRight.moveId
                                        5 -> editState.getDeckInSaved()!!.optionalUpperLeft.moveId
                                        6 -> editState.getDeckInSaved()!!.optionalLowerLeft.moveId
                                        7 -> editState.getDeckInSaved()!!.optionalLowerRight.moveId
                                        else -> -1
                                    }
                                ),
                                isUseMirror = when (argMsg.toSelectMsg.whatBarToEdit) {
                                    4 -> editState.getDeckInSaved()!!.optionalUpperRight.isUseMirror
                                    5 -> editState.getDeckInSaved()!!.optionalUpperLeft.isUseMirror
                                    6 -> editState.getDeckInSaved()!!.optionalLowerLeft.isUseMirror
                                    7 -> editState.getDeckInSaved()!!.optionalLowerRight.isUseMirror
                                    else -> 0
                                }
                            )
                        }

                    }
                    whenClickMoveInOneBar()
                }


            }
        }
        // viewStub加载完成之后，要在onViewCreate那里才能找到加载的view
        dataBinding?.moveSelectViewStub?.viewStub?.inflate()
        dataBinding?.apply {
            moveSelectPager?.apply {
                adapter = movePagerAdapter
                registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                    }
                })
            }
            val iconList = listOf<Int>(R.drawable.ic_upper_right_bold,R.drawable.ic_upper_left_bold,
            R.drawable.ic_lower_left_bold,R.drawable.ic_lower_right_bold)
            if (moveSelectTab != null) {
                if (moveSelectPager != null) {
                    TabLayoutMediator(moveSelectTab,moveSelectPager){tab, position ->
                        tab.setIcon(iconList[position])
                    }.attach()
                }
            }
            // 设置筛选spinner
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

        try {
            barLazy = requireView().findViewById(R.id.moveSelect_bar) as ViewGroup// 拿到布局 这个流程必须再onViewCreated这里进行
            // TODO: 在这里要给move的img加点击事件，告诉下面的recycleFragment startSide是什么，用flow传递过去
            if (seqPack != null){
                barLazy?.apply {
                    sideStart = findViewById(R.id.bar_move_side_0)
                    side1 = findViewById(R.id.bar_move_side_1)
                    side2 = findViewById(R.id.bar_move_side_2)
                    sideEnd = findViewById(R.id.bar_move_side_3)

                    sideStart?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                    side1?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                    side2?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                    sideEnd?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))

                    move0 = findViewById(R.id.bar_move_0)
                    move1 = findViewById(R.id.bar_move_1)
                    move2 = findViewById(R.id.bar_move_2)

                    moveImgList.clear()
                    moveImgList.addAll(listOf(move0,move1,move2))// 最多最多放三个，多一个都不行，所以上面的站架图片就别搞list了，好好的一个一个写
                    moveImgList.forEachIndexed { index, img ->// 设置每一个move的点击事件
                        img?.setOnClickListener {
                            whenClickMoveInBar(index)
                        }
                        img?.setOnLongClickListener { _->
                            whenLongClickMove(index)
                            return@setOnLongClickListener true
                        }
                    }
                }
                changeMovesBar(0,1,2)
            } else if (optionPack != null){
                barLazy?.apply {
                    sideStart = findViewById(R.id.bar_oneMove_side_start)
                    sideEnd = findViewById(R.id.bar_oneMove_side_end)
                    move0 = findViewById(R.id.bar_oneMove_img)
                    move0?.setOnClickListener { whenClickMoveInOneBar() }
                    move0?.setOnLongClickListener {_->
                        whenLongClickMove()
                        return@setOnLongClickListener true
                    }
                }
                changeOneMoveBar()
                move0?.strokeWidth = resources.getDimension(R.dimen.moveShapeableImgStrokeWidth)
            }

        } catch (e :Exception){
            Log.e(TAG, "onViewCreated: ", e)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.moveBeClickFlow.collectLatest {
                    if (seqPack != null){// 只有序列攻击才要设置选择边框
                        Log.i(TAG, "onViewCreated: 接受到选择边框变化->$it")
                        setMoveInBarBeSelect(it)
                    }
                }
            }
        }

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
                editState.enterSelectFlow.collectLatest {
                    when(it){
                        in 0..2 ->{
                            Log.i(TAG, "enterSelectFlow: 第一次进来")
                            if (seqPack != null){//
                                if (seqPack!!.idList[it] != -1){// 说明选中的框有招式
                                    val tempForSelect = MoveForSelect(
                                        moveOrigin = if (SettingRepository.isUseCNEditionMod) MoveGenerate.emptyMoveOrigin else seqPack!!.originList[it]!!,
                                        moveCE = if (SettingRepository.isUseCNEditionMod) seqPack!!.ceList[it]!! else MoveGenerate.emptyMoveCE,
                                        isSelected = false,
                                        isMirror = seqPack!!.isMirrorList[it]
                                    )
                                    editState.selectMove(tempForSelect)
                                }
                            }
                            if (optionPack != null){
                                if (optionPack!!.optionA != -1){
                                    val tempForSelect = MoveForSelect(
                                        moveOrigin = if (SettingRepository.isUseCNEditionMod) MoveGenerate.emptyMoveOrigin else optionPack!!.optionMove!!,
                                        moveCE = if (SettingRepository.isUseCNEditionMod) optionPack!!.ceMove!! else MoveGenerate.emptyMoveCE,
                                        isSelected = false,
                                        isMirror = optionPack!!.isMirror
                                    )
                                    editState.selectMove(tempForSelect)
                                }
                            }
                            editState.initEnterSelect(-1)// 重新设置成其他数据
                        }
                        else->{
                            Log.i(TAG, "enterSelectFlow: 不是第一次进来，就别设置了")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                editState.moveForSelectFlow.collectLatest {// 接收到数据，首先更新数据部分的文本，然后更新站架信息，最后更新pack的数据，最后找机会变更viewModel
                    when(it){
                        is MoveMsgState.SelectNull -> {
                            Log.i(TAG, "moveForSelectFlow: 接收到 null 的选择")
                            if (it.isEnterFromEdit){
                                Log.i(TAG, "moveForSelectFlow: 从editFragment进来的")
                                return@collectLatest
                            }
                            if (seqPack != null){
                                Log.i(TAG, "moveForSelectFlow->: 第${editState.moveBeClickFlow.value}个置空")
                                seqPack!!.updateOne(editState.moveBeClickFlow.value,null)
                                moveImgList[editState.moveBeClickFlow.value]?.setImageResource(R.drawable.ic_add_move)
                                moveImgList[editState.moveBeClickFlow.value]?.setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                                when(editState.moveBeClickFlow.value){
                                    0 ->{// 起始站架已经定死，看看第二个框有没有招式
                                        if (seqPack!!.idList[1] == -1){// 没有就修改结束站架
                                            side1?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                                        }
                                    }
                                    1 ->{// 移除的是中间的，则前后都要看
                                        if (seqPack!!.idList[0] == -1){// 第一个没有招式，改
                                            side1?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                                        }
                                        if (seqPack!!.idList[2] == -1){// 第三个没招式，改
                                            side2?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                                        }
                                    }
                                    2 ->{// 移除的是最后一个
                                        if (seqPack!!.idList[1] == -1){// 第二个没有招式，改
                                            side2?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))
                                        }
                                        sideEnd?.setImageResource(SideUtil.imgIdForMoves(seqPack!!.startSide))// 结束站架可以直接修改
                                    }
                                }
                            }
                            if (optionPack != null){
                                optionPack!!.updateOptByMoveForSelect(null)
                                sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(optionPack!!.startSide)))
                                move0?.apply {
                                    setImageResource(R.drawable.ic_add_move)
                                    setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                                }
                            }
                            removeMsg()
                            updateDeckInSaveState()
                        }
                        is MoveMsgState.SelectOne -> {
                            if (SettingRepository.isUseCNEditionMod){
                                setMoveCEMsg(it.moveForSelect)
                            } else{
                                setMoveMsg(it.moveForSelect)
                            }
                            // 然后还要更新bar的布局
                            if (seqPack != null){
                                moveImgList[editState.moveBeClickFlow.value]?.setImageBitmap(
                                    AssetsUtil.getBitmapByMoveId(requireContext(),
                                        if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.id } else { it.moveForSelect.moveOrigin.id })
                                )
                                when(editState.moveBeClickFlow.value){// 根据选择的招式来修改起始结束站架
                                    0 ->{// 起始站架已经定死，只用修改结束站架side1
                                        side1?.setImageResource(SideUtil.imgIdForMoves(
                                            if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.endSide } else { it.moveForSelect.moveOrigin.endSide }
                                        ))
                                    }
                                    1 ->{
                                        side1?.setImageResource(SideUtil.imgIdForMoves(
                                            if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.startSide } else { it.moveForSelect.moveOrigin.startSide }
                                        ))
                                        side2?.setImageResource(SideUtil.imgIdForMoves(
                                            if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.endSide } else { it.moveForSelect.moveOrigin.endSide }
                                        ))
                                    }
                                    2 ->{
                                        side2?.setImageResource(SideUtil.imgIdForMoves(
                                            if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.startSide } else { it.moveForSelect.moveOrigin.startSide }
                                        ))
                                        sideEnd?.setImageResource(SideUtil.imgIdForMoves(
                                            if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.endSide } else { it.moveForSelect.moveOrigin.endSide }
                                        ))
                                    }
                                }
                                seqPack!!.updateOne(editState.moveBeClickFlow.value,it.moveForSelect)
                                Log.i(TAG, "moveForSelectFlow: idList:${seqPack!!.idList} isMirrorList:${seqPack!!.isMirrorList}")
                                // 这里要更新editState里面存在saveState中的deck数据就行
                            }else if (optionPack != null){// 起始站架已经定死，所以只用修改结束站架
                                move0?.setImageBitmap(AssetsUtil.getBitmapByMoveId(requireContext(),
                                    if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.id } else { it.moveForSelect.moveOrigin.id }
                                ))
                                sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(
                                    if (SettingRepository.isUseCNEditionMod){ it.moveForSelect.moveCE.endSide } else { it.moveForSelect.moveOrigin.endSide }
                                )))
                                optionPack!!.updateOptByMoveForSelect(moveForSelect = it.moveForSelect)
                                Log.i(TAG, "moveForSelectFlow: ${optionPack!!}")
                            }
                            updateDeckInSaveState()
                        }
                        else -> {
                            Log.i(TAG, "moveForSelectFlow: 不知道是什么类型")
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

    private fun setMoveInBarBeSelect(moveIndex :Int){
        moveImgList.forEachIndexed { index, img ->
            if (index == moveIndex){
                img?.strokeWidth = resources.getDimension(R.dimen.moveShapeableImgStrokeWidth)
            } else {
                img?.strokeWidth = 0F
            }
        }
    }

    // 当bar里面的moveImg被选中的时候，应该向recycle发送一个信号（用flow），告诉它结束站架是什么，
    // 假如结束站架已经被约束（后面有已经选好的招式，然后后面的起始站架限定了这次你选的这个招式的结束站架）这时候就得滑到那个站架的tab，然后锁定住ViewPager，最后给recycle传递一个结束站架的约束
    // 假如起始站架被约束（前面限定了起始站架）则给recycle传一个起始站架的约束
    // 假如起始结束都被限定死了，那就两个都传递过去
    // 假如都没限定，则传递all就行
    private fun whenClickMoveInBar(moveIndex: Int){
        editState.selectWhatMoveInSeq(moveIndex)
        dataBinding?.moveSelectPager?.isUserInputEnabled = true// 先解锁
        // 在这里判断是否有限制，然后分发sideLimit
        CoroutineScope(Dispatchers.Main).launch { // 进CPU密集型线程来计算，别阻塞主线程
            seqPack?.apply {
                when(moveIndex){
                    0 ->{// 选择了序列的第一个，起始站架已经被定死，但还要判断结束站架是否被第二个招式(还可能为空)限制
                        val tempStart = when(argMsg.toSelectMsg.whatBarToEdit){
                            0 -> StandSide.UPPER_RIGHT
                            1 -> StandSide.UPPER_LEFT
                            2 -> StandSide.LOWER_LEFT
                            3 -> StandSide.LOWER_RIGHT
                            else -> StandSide.UPPER_RIGHT
                        }
                        if (idList[1] != -1){// 说明第二个槽位有招式，它的起始站架限制了这个结束站架
                            // TODO: 要限制tab只能处于结束站架限定的那个viewPage
                            val tempEnd = if (SettingRepository.isUseCNEditionMod){ ceList[1]!!.startSide } else { originList[1]!!.startSide }
                            editState.updateSideLimit(
                                SideLimit.limitAll(startSide = tempStart, endSide = tempEnd)
                            )
                            dataBinding?.moveSelectPager?.setCurrentItem(SideUtil.getIntBySide(tempEnd),true)// 滚动到结束站架限定的viewPager
                            dataBinding?.moveSelectPager?.isUserInputEnabled = false
                        } else {
                            editState.updateSideLimit(SideLimit.limitStart(startSide = tempStart))
                        }
                    }
                    1 ->{// 选择了第二个，所有情况均有可能
                        if (idList[0] != -1){// 说明第一个槽位有招式,它的结束站架限制了第二个招式的起始站架
                            val tempStart = if (SettingRepository.isUseCNEditionMod) { ceList[0]!!.endSide } else { originList[0]!!.endSide }
                            if (idList[2] != -1){// 说明第三个槽位有招式，它的起始站架限制了第二个招式的结束站架
                                // TODO: 要限制tab只能处于结束站架限定的那个viewPage
                                val tempEnd = if (SettingRepository.isUseCNEditionMod) { ceList[2]!!.startSide } else { originList[2]!!.startSide }
                                editState.updateSideLimit(SideLimit.limitAll(startSide = tempStart, endSide = tempEnd))
                                dataBinding?.moveSelectPager?.setCurrentItem(SideUtil.getIntBySide(tempEnd),true)// 滚动到结束站架限定的viewPager
                                dataBinding?.moveSelectPager?.isUserInputEnabled = false
                            } else {
                                // 第三个槽位为空，说明只限制了起始站架
                                editState.updateSideLimit(SideLimit.limitStart(startSide = tempStart))
                            }
                        } else {// 第一个槽位为空，则起始站架没被限制
                            if (idList[2] != -1){// 说明第三个槽位有招式，它的起始站架限制了第二个招式的结束站架
                                // TODO: 这里要限制tab只能处于结束站架限定的那个viewPage
                                val tempEnd = if (SettingRepository.isUseCNEditionMod){ ceList[2]!!.startSide } else { originList[2]!!.startSide }
                                editState.updateSideLimit(SideLimit.limitEnd(endSide = tempEnd))
                                dataBinding?.moveSelectPager?.setCurrentItem(SideUtil.getIntBySide(tempEnd),true)// 滚动到结束站架限定的viewPager
                                dataBinding?.moveSelectPager?.isUserInputEnabled = false
                            } else {
                                // 第三个槽位为空,结束站架也没有任何限制
                                editState.updateSideLimit(SideLimit.noLimit())
                            }
                        }
                    }
                    2 ->{// 选择了第三个，结束站架不会被限制，只看第二个招式的限制
                        editState.updateSideLimit(
                            if (idList[1] != -1) {// 说明第二个槽位有招式，它的结束站架限制了第三个招式的起始站架
                                SideLimit.limitStart(startSide = if (SettingRepository.isUseCNEditionMod){ ceList[1]!!.endSide } else { originList[1]!!.endSide })
                            } else {
                                // 第二个槽位为空，则起始站架没有任何限制
                                SideLimit.noLimit()
                            }
                        )
                    }
                }
            }
        }
    }
    private fun whenClickMoveInOneBar(){
        CoroutineScope(Dispatchers.Main).launch {
            if (optionPack != null){
                // 起始站架总是会被限制，结束站架被限制成不能和起始站架一样的数据
                // TODO: 应该设置tab不能跳到和起始站架一样的viewPage
                // 不需要限制了因为经过筛选后和起始站架一样的pager的数据直接为空，选不了就不会出事
                editState.updateSideLimit(SideLimit.optLimit(startSide = optionPack!!.startSide))
            }
        }

    }
    // 长按招式框，可以把这个框和对应的seqPack还有editState里面的deck的数据也一起变更了
    private fun whenLongClickMove(moveIndex: Int = 0){
        Log.i(TAG, "whenLongClickMove: $moveIndex 触发了长按删除")
        editState.selectWhatMoveInSeq(moveIndex)
        editState.selectNull()
    }

    private fun setMoveMsg(moveForSelect: MoveForSelect) {
        val tempMove = moveForSelect.moveOrigin
        dataBinding?.moveSelectInclude?.apply {
            // 设置第一部分
            GlideApp.with(msgImg)
                .load(AssetsUtil.getBitmapByMoveId(requireContext(), moveId = tempMove.id))
                .into(msgImg)
            msgImg.setBackgroundColor(resources.getColor(if (tempMove.id > 197) R.color.img_add_move_bg else R.color.transparent))

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
    private fun setMoveCEMsg(moveForSelect: MoveForSelect) {
        val tempMove = moveForSelect.moveCE
        dataBinding?.moveSelectInclude?.apply {
            // 设置第一部分
            GlideApp.with(msgImg)
                .load(AssetsUtil.getBitmapByMoveId(requireContext(), moveId = tempMove.id))
                .into(msgImg)
            msgImg.setBackgroundColor(resources.getColor(if (tempMove.id > 197) R.color.img_add_move_bg else R.color.transparent))

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

    private fun doAfterChangeFilterManual(){// 应该在发射这里进行拦截，确保collect的时候总会更新，然后recycle那边也不会收到很多次
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

    /**根据[seqPack]里面的数据变更bar*/
    private fun changeMovesBar(@androidx.annotation.IntRange(0,2) vararg changePosition :Int){
        seqPack?.apply {
            changePosition.forEachIndexed { index, position ->
                if (SettingRepository.isUseCNEditionMod){// 使用mod的情况下
                    if (ceList[index] != null && idList[position] != -1){
                        ceList[index]!!.apply {
                            moveImgList[index]?.setImageBitmap(
                                AssetsUtil.getBitmapByMoveId(requireContext(),id)
                            )
                            when(position){// 根据选择的招式来修改起始结束站架
                                0 ->{// 起始站架已经定死，只用修改结束站架side1
                                    side1?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                                1 ->{
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                    side2?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                                2 ->{
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                    sideEnd?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                            }
                        }
                    } else{
                        // 恢复招式img为初始化
                        moveImgList[index]?.apply {
                            setImageResource(R.drawable.ic_add_move)
                            setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                        }
                        when(position){// 恢复站架图标，需要按照前后是否有招式判断,恢复的是按起始站架来变
                            0 ->{// 只用看第二个有没有招式有就不变，没就变
                                if (idList[1] == -1){
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                            1 ->{// 要看前后，而且是分开来看
                                if (idList[0] == -1){
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                                if (idList[2] == -1){
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                            2 ->{// 看中间那个，第二个
                                if (idList[1] == -1){
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                        }
                    }
                } else {// 不使用mod的情况下
                    if (originList[index] != null && idList[position] != -1){
                        originList[index]!!.apply {
                            moveImgList[index]?.setImageBitmap(
                                AssetsUtil.getBitmapByMoveId(requireContext(),id)
                            )
                            when(position){// 根据选择的招式来修改起始结束站架
                                0 ->{// 起始站架已经定死，只用修改结束站架side1
                                    side1?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                                1 ->{
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                    side2?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                                2 ->{
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                    sideEnd?.setImageResource(SideUtil.imgIdForMoves(endSide))
                                }
                            }
                        }
                    } else{
                        // 恢复招式img为初始化
                        moveImgList[index]?.apply {
                            setImageResource(R.drawable.ic_add_move)
                            setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                        }
                        when(position){// 恢复站架图标，需要按照前后是否有招式判断,恢复的是按起始站架来变
                            0 ->{// 只用看第二个有没有招式有就不变，没就变
                                if (idList[1] == -1){
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                            1 ->{// 要看前后，而且是分开来看
                                if (idList[0] == -1){
                                    side1?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                                if (idList[2] == -1){
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                            2 ->{// 看中间那个，第二个
                                if (idList[1] == -1){
                                    side2?.setImageResource(SideUtil.imgIdForMoves(startSide))
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    /**根据[optionPack]里面的数据来变更*/
    private fun changeOneMoveBar(){
        optionPack?.apply {
            sideStart?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(this.startSide)))
            if (SettingRepository.isUseCNEditionMod){
                if (ceMove != null && optionA != -1){
                    ceMove!!.let {move->
                        move0?.setImageBitmap(AssetsUtil.getBitmapByMoveId(requireContext(),move.id))
                        sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(move.endSide)))
                    }
                } else{
                    move0?.apply {
                        setImageResource(R.drawable.ic_add_move)
                        setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                    }
                    sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(this.startSide)))
                }
            } else {
                if (optionMove != null && optionA != -1){
                    optionMove!!.let {move->
                        move0?.setImageBitmap(AssetsUtil.getBitmapByMoveId(requireContext(),move.id))
                        sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(move.endSide)))
                    }
                } else{
                    move0?.apply {
                        setImageResource(R.drawable.ic_add_move)
                        setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
                    }
                    sideEnd?.setImageResource(SideUtil.imgIdForOneMove(SideUtil.getIntBySide(this.startSide)))
                }
            }

        }
    }


    private fun updateDeckInSaveState(){
        if (seqPack != null){
            when(argMsg.toSelectMsg.whatBarToEdit){
                0->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.sequenceUpperRight = seqPack!!.idList.zip(seqPack!!.isMirrorList).map {
                            MoveBox(moveId = it.first, isUseMirror = it.second)
                        }.toMutableList()
                    })
                }
                1->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.sequenceUpperLeft = seqPack!!.idList.zip(seqPack!!.isMirrorList).map {
                            MoveBox(moveId = it.first, isUseMirror = it.second)
                        }.toMutableList()
                    })
                }
                2->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.sequenceLowerLeft = seqPack!!.idList.zip(seqPack!!.isMirrorList).map {
                            MoveBox(moveId = it.first, isUseMirror = it.second)
                        }.toMutableList()
                    })
                }
                3->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.sequenceLowerRight = seqPack!!.idList.zip(seqPack!!.isMirrorList).map {
                            MoveBox(moveId = it.first, isUseMirror = it.second)
                        }.toMutableList()
                    })
                }
            }
        } else if (optionPack != null){
            when(argMsg.toSelectMsg.whatBarToEdit){
                4->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.optionalUpperRight = MoveBox(moveId = optionPack!!.optionA, isUseMirror = optionPack!!.isMirror)
                    })
                }
                5->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.optionalUpperLeft = MoveBox(moveId = optionPack!!.optionA, isUseMirror = optionPack!!.isMirror)
                    })
                }
                6->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.optionalLowerLeft = MoveBox(moveId = optionPack!!.optionA, isUseMirror = optionPack!!.isMirror)
                    })
                }
                7->{
                    editState.saveDeckInSaved(editState.getDeckInSaved()!!.apply {
                        this.optionalLowerRight = MoveBox(moveId = optionPack!!.optionA, isUseMirror = optionPack!!.isMirror)
                    })
                }
            }
        }
    }

}