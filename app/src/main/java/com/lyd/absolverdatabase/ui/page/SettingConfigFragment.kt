package com.lyd.absolverdatabase.ui.page

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.chad.library.adapter.base.dragswipe.QuickDragAndSwipe
import com.chad.library.adapter.base.dragswipe.listener.OnItemDragListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.FilterItem
import com.lyd.absolverdatabase.bridge.data.bean.UseTheme
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingConfigBinding
import com.lyd.absolverdatabase.ui.adapter.MovesFilterAdapter
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.isNightMode
import com.lyd.absolverdatabase.utils.restartApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingConfigFragment :BaseFragment() {

    private var configBinding :FragmentSettingConfigBinding ?= null
    private val settingState :SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private val restartSnackBar by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Snackbar.make(configBinding!!.settingConfigChipGroupTheme,R.string.restart_app_in_time,Snackbar.LENGTH_SHORT)
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
    private val filterAdapter :MovesFilterAdapter by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        MovesFilterAdapter().apply {
            addOnItemChildClickListener(R.id.item_movesFilter_checkbox){ adapter, view, position->
                val tempCheckBox :MaterialCheckBox= view as MaterialCheckBox
                adapter.getItem(position)?.isChecked = tempCheckBox.isChecked
                settingState.changeMovesFilterJson(filterList)
                llog.d(TAG,"点击了第${position}个item check状态->${tempCheckBox.isChecked} ${adapter.getItem(position)}")
            }
        }
    }
    private val quickDrag :QuickDragAndSwipe by lazy {
        QuickDragAndSwipe().setDragMoveFlags(ItemTouchHelper.UP or
                ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }
    private val dragListener :OnItemDragListener by lazy {
        object :OnItemDragListener{
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
        val view :View = inflater.inflate(R.layout.fragment_setting_config,container,false)

        configBinding = FragmentSettingConfigBinding.bind(view)
        configBinding?.lifecycleOwner = viewLifecycleOwner

        configBinding?.apply {
            ViewCompat.setTransitionName(settingConfigTitle,"ConfigTitle")

            settingConfigSwitchIsUseToolbar.setOnCheckedChangeListener{ btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseToolbar(isChecked)
                (mActivity as MainActivity).changeToolbar()
            }
            settingConfigSwitchGaussianBlur.apply {
                setOnCheckedChangeListener { btn, isChecked ->
                    if (!btn.isPressed) return@setOnCheckedChangeListener
                    settingState.changeGaussianBlurFlow(isChecked)
                }
                if (Build.VERSION.SDK_INT < 31){// 如果低于31，高斯模糊不生效，所以只能这样
                    isChecked = false
                    isEnabled = false
                    settingState.changeGaussianBlurFlow(false)
                }
            }
            settingConfigSwitchAskBeforeImport.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeAskBeforeImportDeck(isChecked)
            }
            settingConfigSwitchShowSeqDetail.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeShowSeqDetailWhenSharedDeck(isChecked)
            }
            settingConfigSwitchUseShareSheet.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseShareSheetWhenSharedDeck(isChecked)
            }
            settingConfigSwitchShowMovesMsgInDeckEdit.setOnCheckedChangeListener{ btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeShowMovesMsgInDeckEdit(isChecked)
            }
            settingConfigChipGroupWhatMsgInDeckEdit.setOnCheckedStateChangeListener{ group, checkedIds ->
                if (checkedIds.isEmpty())
                    return@setOnCheckedStateChangeListener
                val tempBtn = group.findViewById<Chip>(checkedIds[0])
                if (!tempBtn.isPressed) {
                    return@setOnCheckedStateChangeListener
                }
                when(checkedIds[0]){
                    R.id.settingConfig_chip_frameMsg -> settingState.changeShowWhatMsgInDeck(0)
                    R.id.settingConfig_chip_trendMsg -> settingState.changeShowWhatMsgInDeck(1)
                }
            }
            settingConfigRadioGroupModSelect.setOnCheckedChangeListener { group, checkedId ->
                val tempBtn = group.findViewById<RadioButton>(checkedId)
                if (!tempBtn.isPressed){
                    return@setOnCheckedChangeListener
                }
                when(checkedId){
                    R.id.settingConfig_radioBtn_origin ->{
                        settingState.changeUseWhatDataMod(SettingRepository.ORIGIN)
                    }
                    R.id.settingConfig_radioBtn_ce ->{
                        settingState.changeUseWhatDataMod(SettingRepository.CEMOD)
                    }
                }
            }
            settingConfigSwitchShowMoreMoveCEInfo.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeShowMoreMoveCEInfo(isChecked)
            }
            settingConfigSwitchUseNightMode.setOnCheckedChangeListener{ btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseNightMode(isChecked)
            }

            val gridLayoutManager :GridLayoutManager = GridLayoutManager(context,4)
            settingConfigRecycleFilter.layoutManager = gridLayoutManager
            settingConfigRecycleFilter.adapter = filterAdapter

            filterList.apply {
                clear()
                addAll(GsonUtils.fromJson(SettingRepository.movesFilterListJson,GsonUtils.getListType(FilterItem::class.java)))
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

            quickDrag.attachToRecyclerView(settingConfigRecycleFilter)
                .setDataCallback(filterAdapter)
                .setItemDragListener(dragListener)


            if (requireContext().isNightMode()){
                settingConfigChipDefault.setTextColor(requireContext().getColor(com.google.android.material.R.color.design_dark_default_color_primary))
                settingConfigChipRed.setTextColor(resources.getColor(R.color.red_md_theme_dark_primary))
                settingConfigChipGold.setTextColor(resources.getColor(R.color.gold_md_theme_dark_primary))
                settingConfigChipBlue.setTextColor(resources.getColor(R.color.blue_md_theme_dark_primary))
                settingConfigChipGreen.setTextColor(resources.getColor(R.color.green_md_theme_dark_primary))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                settingConfigChipDefault.isEnabled = false
                settingConfigChipWallpaper.isEnabled = false
                settingConfigChipRed.isEnabled = false
                settingConfigChipGold.isEnabled = false
                settingConfigChipBlue.isEnabled = false
                settingConfigChipGreen.isEnabled = false
                settingState.changeUseWhatTheme(UseTheme.DefaultId)
            }
            settingConfigChipGroupTheme.setOnCheckedStateChangeListener { group, checkedIds ->// 由于我是singleSelect，所以这个ids只有一个
                if (checkedIds.isEmpty())
                    return@setOnCheckedStateChangeListener
                val tempBtn = group.findViewById<Chip>(checkedIds[0])
                if (!tempBtn.isPressed) {
                    return@setOnCheckedStateChangeListener
                }
                when(checkedIds[0]){
                    R.id.settingConfig_chip_default ->{
                        settingState.changeUseWhatTheme(UseTheme.DefaultId)
                    }
                    R.id.settingConfig_chip_wallpaper ->{
                        settingState.changeUseWhatTheme(UseTheme.WallpaperId)
                    }
                    R.id.settingConfig_chip_red ->{
                        settingState.changeUseWhatTheme(UseTheme.RedId)
                    }
                    R.id.settingConfig_chip_gold ->{
                        settingState.changeUseWhatTheme(UseTheme.YellowId)
                    }
                    R.id.settingConfig_chip_blue ->{
                        settingState.changeUseWhatTheme(UseTheme.BlueId)
                    }
                    R.id.settingConfig_chip_green ->{
                        settingState.changeUseWhatTheme(UseTheme.GreenId)
                    }
                }
                lifecycleScope.launchWhenStarted {
                    settingConfigChipDefault.isEnabled = false
                    settingConfigChipWallpaper.isEnabled = false
                    settingConfigChipRed.isEnabled = false
                    settingConfigChipGold.isEnabled = false
                    settingConfigChipBlue.isEnabled = false
                    settingConfigChipGreen.isEnabled = false
                    group.findViewById<Chip>(checkedIds[0]).isEnabled = true
                    restartSnackBar.show()
                    delay(1000)
                    context?.restartApp()
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 在这里进行flow的监听
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useToolbarFlow.collectLatest {
                    llog.i(TAG, "是否使用Toolbar -> $it")
                    configBinding?.settingConfigSwitchIsUseToolbar?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.dialogGaussianBlurFlow.collectLatest {
                    llog.i(TAG, "dialogGaussianBlurFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchGaussianBlur?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.askBeforeImportDeckFlow.collectLatest {
                    llog.i(TAG, "askBeforeImportDeckFlow: flow->$it")
                    configBinding?.settingConfigSwitchAskBeforeImport?.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showSeqDetailWhenSharedDeckFlow.collectLatest {
                    llog.i(TAG, "showSeqDetailWhenSharedDeckFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchShowSeqDetail?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useShareSheetWhenSharedDeckFlow.collectLatest {
                    llog.i(TAG,"useShareSheetWhenSharedDeckFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchUseShareSheet?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showMovesMsgInDeckEditFlow.collectLatest {
                    llog.i(TAG,"showMovesMsgInDeckEditFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchShowMovesMsgInDeckEdit?.isChecked = it
                    configBinding?.settingConfigChipGroupWhatMsgInDeckEdit?.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showWhatMsgInDeckEditFlow.collectLatest {
                    llog.i(TAG,"showWhatMsgInDeckEditFlow: 接收到数据 $it")
                    configBinding?.apply {
                        when(it){
                            0 -> settingConfigChipFrameMsg.isChecked = true
                            1 -> settingConfigChipTrendMsg.isChecked = true
                        }
                    }
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useWhatDataModFlow.collectLatest {
                    llog.i(TAG,"使用数据模式 -> $it")
                    configBinding?.apply {
                        when(it){
                            SettingRepository.ORIGIN ->settingConfigRadioGroupModSelect.check(R.id.settingConfig_radioBtn_origin)
                            SettingRepository.CEMOD ->settingConfigRadioGroupModSelect.check(R.id.settingConfig_radioBtn_ce)
                        }
                        settingConfigSwitchShowMoreMoveCEInfo.visibility = if (it == SettingRepository.CEMOD) View.VISIBLE else View.GONE
                        if (it != SettingRepository.CEMOD) settingState.changeShowMoreMoveCEInfo(false)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showMoreMoveCEInfoFlow.collectLatest {
                    llog.i(TAG, "showMoreMoveCEInfoFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchShowMoreMoveCEInfo?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useNightModeFlow.collectLatest {
                    llog.i(TAG, "useNightModeFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchUseNightMode?.apply {
                        isChecked = it
                        text = if (it) getString(R.string.nightMode_yes) else getString(R.string.nightMode_follow_system)
                        when(it){
                            true -> {
                                if (!context.isNightMode()){
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                }
                            }
                            false -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useWhatThemeFlow.collectLatest {
                    llog.i(TAG, "useWhatThemeFlow: 接收到数据 $it")
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ){
                        configBinding?.settingConfigChipGroupTheme?.check(R.id.settingConfig_chip_default)
                        return@collectLatest
                    }
                    configBinding?.apply {
                        when(it){
                            UseTheme.DefaultId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_default) }
                            UseTheme.WallpaperId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_wallpaper) }
                            UseTheme.RedId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_red) }
                            UseTheme.YellowId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_gold) }
                            UseTheme.BlueId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_blue) }
                            UseTheme.GreenId ->{ settingConfigChipGroupTheme.check(R.id.settingConfig_chip_green) }
                        }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        configBinding = null
    }
}