package com.lyd.absolverdatabase.ui.page

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import com.chad.library.adapter4.dragswipe.listener.OnItemDragListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.FilterItem
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.ui.adapter.MovesFilterAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.GsonUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingAdvanceFragment :BaseFragment() {

    private val settingState : SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private var textTitle :TextView ?= null
    private var switchGaussian :MaterialSwitch ?= null
    private var switchAskBeforeImport :MaterialSwitch ?= null
    private var switchUseShareSheet :MaterialSwitch ?= null
    private var switchShowSeqDetail :MaterialSwitch ?= null
    private lateinit var linearWhichUsedMoveTag :LinearLayout
    private lateinit var textUsedMoveTag :TextView
    private lateinit var switchVibrate :MaterialSwitch
    private lateinit var linearVibrateParams :LinearLayout
    private lateinit var sliderVibrateTime :Slider
    private lateinit var sliderVibrateStrength :Slider


    private lateinit var recycleFilter :RecyclerView

    private var sliderMoveItems :Slider ?= null

    private val onCheckedChange : CompoundButton.OnCheckedChangeListener by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@OnCheckedChangeListener
            when(btn.id){
                R.id.settingAdvance_switch_gaussianBlur ->{ settingState.changeGaussianBlurFlow(isChecked) }
                R.id.settingAdvance_switch_askBeforeImport ->{ settingState.changeAskBeforeImportDeck(isChecked) }
                R.id.settingAdvance_switch_showSeqDetail ->{ settingState.changeShowSeqDetailWhenSharedDeck(isChecked) }
                R.id.settingAdvance_switch_useShareSheet ->{ settingState.changeUseShareSheetWhenSharedDeck(isChecked) }
                R.id.settingAdvance_switch_isUseVibrate ->{ settingState.changeUseVibrate(isChecked) }
            }
        }
    }
    private val onSlideChange :Slider.OnChangeListener by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Slider.OnChangeListener { slider, value, fromUser ->
            if (!fromUser) return@OnChangeListener
            when(slider.id){
                R.id.settingAdvance_slider_movesItems_in_oneRow->{ settingState.changeMoveItemsInOneRow(value.toInt()) }
                R.id.settingAdvance_slider_vibrateTime->{
                    settingState.changeVibrateParams(value.toInt() * 1000 + SettingRepository.vibrateParams%1000)
                }
                R.id.settingAdvance_slider_vibrateStrength->{
                    settingState.changeVibrateParams((SettingRepository.vibrateParams/1000) * 1000 + value.toInt())
                    llog.d(msg = "onChange vibrate strength:${value.toInt()} param:${SettingRepository.vibrateParams}")
                    val time = 10L
                    val strength = SettingRepository.vibrateParams % 1000
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                        val v = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        if (v.defaultVibrator.hasVibrator()){
                            // 这里貌似并没有什么作用
                            VibrationEffect.startComposition()
                            v.defaultVibrator.vibrate(VibrationEffect.createOneShot(time.toLong(),strength))
                        }
                    } else {
                        val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (v.hasVibrator()){
                            v.vibrate(VibrationEffect.createOneShot(time.toLong(),strength))
                        }
                    }
                }
            }
        }
    }

    private val useFrameForTagSnackbar :Snackbar by lazy {
        Snackbar.make(linearWhichUsedMoveTag,getString(R.string.useShapeTag_tip),Snackbar.LENGTH_SHORT)
    }
    private val usedMoveTagPopup :PopupMenu by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        PopupMenu(requireContext(),textUsedMoveTag).apply {
            menuInflater.inflate(R.menu.menu_used_move_tag,menu)
            setOnMenuItemClickListener {
                settingState.changeWhichSignTag(when(it.itemId){
                    R.id.menu_usedMoveTag_shape -> 0
                    R.id.menu_usedMoveTag_img -> 1
                    else -> 0
                })
                if (it.itemId == R.id.menu_usedMoveTag_shape && !useFrameForTagSnackbar.isShownOrQueued){
                    useFrameForTagSnackbar.show()
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private val filterList :MutableList<FilterItem> by lazy {
        mutableListOf(
            FilterItem(FilterItem.STRENGTH, isChecked = true, name = getString(R.string.movesFilter_strength)),
            FilterItem(FilterItem.RANGE, isChecked = true, name = getString(R.string.movesFilter_range)),
            FilterItem(FilterItem.EFFECT, isChecked = true, name = getString(R.string.movesFilter_effect)),
            FilterItem(FilterItem.START_FRAME, isChecked = true, name = getString(R.string.movesFilter_startFrame)),
            FilterItem(FilterItem.PHYSICAL_WEAKNESS, isChecked = true, name = getString(R.string.movesFilter_phyWeakness)),
            FilterItem(FilterItem.PHYSICAL_OUTPUT, isChecked = true, name = getString(R.string.movesFilter_phyOutput)),
            FilterItem(FilterItem.HIT_ADVANTAGE_FRAME, isChecked = true, name = getString(R.string.movesFilter_hitAdvantage)),
            FilterItem(FilterItem.DEF_ADVANTAGE_FRAME, isChecked = true, name = getString(R.string.movesFilter_defAdvantage))
        )
    }
    private val filterAdapter : MovesFilterAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MovesFilterAdapter().apply {
            addOnItemChildClickListener(R.id.item_movesFilter_checkbox){ adapter, view, position->
                val tempCheckBox : MaterialCheckBox = view as MaterialCheckBox
                adapter.getItem(position)?.isChecked = tempCheckBox.isChecked
                settingState.changeMovesFilterJson(filterList)
                llog.d(TAG,"点击了第${position}个item check状态->${tempCheckBox.isChecked} ${adapter.getItem(position)}")
            }
        }
    }
    private val quickDrag : QuickDragAndSwipe by lazy {
        QuickDragAndSwipe().setDragMoveFlags(
            ItemTouchHelper.UP or
                ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }
    private val dragListener : OnItemDragListener by lazy {
        object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }
            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder,
                from: Int,
                target: RecyclerView.ViewHolder,
                to: Int
            ) {

            }
            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                filterList.forEachIndexed { index, filterItem ->
                    llog.d(TAG,"No.$index origin->$filterItem adapter->${filterAdapter.getItem(index)}")
                }
                settingState.changeMovesFilterJson(filterList)
                llog.d(TAG,"---------------拖拽结束----------------------------")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.setting_shared)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_setting_advance,container,false)

        view.apply {
            textTitle = findViewById(R.id.settingAdvance_title)
            switchGaussian = findViewById(R.id.settingAdvance_switch_gaussianBlur)
            switchAskBeforeImport = findViewById(R.id.settingAdvance_switch_askBeforeImport)
            switchUseShareSheet = findViewById(R.id.settingAdvance_switch_useShareSheet)
            switchShowSeqDetail = findViewById(R.id.settingAdvance_switch_showSeqDetail)
            linearWhichUsedMoveTag = findViewById(R.id.settingAdvance_linear_whichUsedMove)
            textUsedMoveTag = findViewById(R.id.settingAdvance_text_usedMoveTag)
            switchVibrate = findViewById(R.id.settingAdvance_switch_isUseVibrate)
            linearVibrateParams = findViewById(R.id.settingAdvance_linear_vibrateParams)
            sliderVibrateTime = findViewById(R.id.settingAdvance_slider_vibrateTime)
            sliderVibrateStrength = findViewById(R.id.settingAdvance_slider_vibrateStrength)

            recycleFilter = findViewById(R.id.settingAdvance_recycle_filter)

            sliderMoveItems = findViewById(R.id.settingAdvance_slider_movesItems_in_oneRow)

            ViewCompat.setTransitionName(textTitle!!,"AdvanceTitle")

            switchGaussian?.apply {
                setOnCheckedChangeListener(onCheckedChange)
                if (Build.VERSION.SDK_INT < 31){// 如果低于31，高斯模糊不生效，所以只能这样
                    isChecked = false
                    isEnabled = false
                    settingState.changeGaussianBlurFlow(false)
                }
            }
            switchAskBeforeImport?.setOnCheckedChangeListener(onCheckedChange)
            switchUseShareSheet?.setOnCheckedChangeListener(onCheckedChange)
            switchShowSeqDetail?.setOnCheckedChangeListener(onCheckedChange)
            linearWhichUsedMoveTag.setOnClickListener{ usedMoveTagPopup.show() }
            switchVibrate.setOnCheckedChangeListener(onCheckedChange)
            sliderVibrateTime.addOnChangeListener(onSlideChange)
            sliderVibrateStrength.addOnChangeListener(onSlideChange)

            val gridLayoutManager : GridLayoutManager = GridLayoutManager(context,4)
            recycleFilter.layoutManager = gridLayoutManager
            recycleFilter.adapter = filterAdapter
            filterList.apply {
                clear()
                addAll(GsonUtils.fromJson(SettingRepository.movesFilterListJson, GsonUtils.getListType(FilterItem::class.java)))
                forEach {
                    when(it.tag){
                        FilterItem.STRENGTH -> it.name = getString(R.string.movesFilter_strength)
                        FilterItem.RANGE -> it.name = getString(R.string.movesFilter_range)
                        FilterItem.EFFECT -> it.name = getString(R.string.movesFilter_effect)
                        FilterItem.START_FRAME -> it.name = getString(R.string.movesFilter_startFrame)
                        FilterItem.PHYSICAL_WEAKNESS -> it.name = getString(R.string.movesFilter_phyWeakness)
                        FilterItem.PHYSICAL_OUTPUT -> it.name = getString(R.string.movesFilter_phyOutput)
                        FilterItem.HIT_ADVANTAGE_FRAME -> it.name = getString(R.string.movesFilter_hitAdvantage)
                        FilterItem.DEF_ADVANTAGE_FRAME -> it.name = getString(R.string.movesFilter_defAdvantage)
                    }
                }
            }
            filterAdapter.submitList(filterList)
            quickDrag.attachToRecyclerView(recycleFilter)
                .setDataCallback(filterAdapter)
                .setItemDragListener(dragListener)

            sliderMoveItems?.addOnChangeListener(onSlideChange)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.dialogGaussianBlurFlow.collectLatest {
                    llog.i(TAG, "dialogGaussianBlurFlow: 接收到数据 $it")
                    switchGaussian?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.askBeforeImportDeckFlow.collectLatest {
                    llog.i(TAG, "askBeforeImportDeckFlow: flow->$it")
                    switchAskBeforeImport?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useShareSheetWhenSharedDeckFlow.collectLatest {
                    llog.i(TAG,"useShareSheetWhenSharedDeckFlow: 接收到数据 $it")
                    switchUseShareSheet?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showSeqDetailWhenSharedDeckFlow.collectLatest {
                    llog.i(TAG, "showSeqDetailWhenSharedDeckFlow: 接收到数据 $it")
                    switchShowSeqDetail?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.whichUsedMoveTagFlow.collectLatest {
                    llog.i(TAG,"whichUsedMoveTagFlow: 接收到数据 $it")
                    textUsedMoveTag.text = getString(when(it){
                        0 -> R.string.usedMoveTag_shape
                        1 -> R.string.usedMoveTag_img
                        else -> R.string.usedMoveTag_shape
                    })
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useVibrateFlow.collectLatest {
                    llog.i(TAG,"useVibrateFlow: 接收到数据 $it")
                    switchVibrate.isChecked = it
                    linearVibrateParams.visibility = if (it) View.VISIBLE else View.GONE
                    if (it){
                        sliderVibrateTime.value = (SettingRepository.vibrateParams/1000).toFloat()
                        sliderVibrateStrength.value = (SettingRepository.vibrateParams%1000).toFloat()
                        llog.i(TAG,"启用震动，显示slider 时间->" + SettingRepository.vibrateParams/1000 + ",振幅->" + SettingRepository.vibrateParams%1000)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.moveItemsInOneRowFlow.collectLatest {
                    llog.i(TAG,"一行内显示招式数量 -> $it")
                    sliderMoveItems?.value = it.toFloat()
                }
            }
        }
    }
}