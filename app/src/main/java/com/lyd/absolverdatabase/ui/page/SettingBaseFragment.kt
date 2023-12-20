package com.lyd.absolverdatabase.ui.page

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navGraphViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.MainActivity
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.UseTheme
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingBaseBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.isNightMode
import com.lyd.absolverdatabase.utils.restartApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingBaseFragment :BaseFragment() {

    private var baseBinding : FragmentSettingBaseBinding?= null
    private val settingState :SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private val modSelectPopup :PopupMenu by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        PopupMenu(requireContext(),baseBinding!!.settingBaseTextMod).apply{
            menuInflater.inflate(R.menu.menu_mods,menu)
            setOnMenuItemClickListener {
                settingState.changeUseWhatDataMod(when(it.itemId){
                    R.id.menu_mod_origin -> SettingRepository.ORIGIN
                    R.id.menu_mod_ce -> SettingRepository.CEMOD
                    else -> SettingRepository.ORIGIN
                })
                return@setOnMenuItemClickListener true
            }
        }
    }
    private val nightModSelectPopup :PopupMenu by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        PopupMenu(requireContext(),baseBinding!!.settingBaseTextNightMod).apply {
            menuInflater.inflate(R.menu.menu_night_mod,menu)
            setOnMenuItemClickListener {
                settingState.changeUseNightMode(when(it.itemId){
                    R.id.menu_night_mode_follow_system -> false
                    R.id.menu_night_mode_yes -> true
                    else -> false
                })
                return@setOnMenuItemClickListener true
            }
        }
    }

    private val restartSnackBar by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Snackbar.make(baseBinding!!.settingBaseChipGroupTheme,R.string.restart_app_in_time,Snackbar.LENGTH_SHORT)
    }

    private val onSwitchCheckedChange : CompoundButton.OnCheckedChangeListener by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@OnCheckedChangeListener
            when(btn.id){
                R.id.settingBase_switch_isUseToolbar -> {
                    settingState.changeUseToolbar(isChecked)
                    (mActivity as MainActivity).changeToolbar()
                }
//                R.id.settingConfig_switch_gaussianBlur ->{ settingState.changeGaussianBlurFlow(isChecked) }
//                R.id.settingConfig_switch_askBeforeImport ->{ settingState.changeAskBeforeImportDeck(isChecked) }
//                R.id.settingConfig_switch_showSeqDetail ->{ settingState.changeShowSeqDetailWhenSharedDeck(isChecked) }
//                R.id.settingConfig_switch_useShareSheet ->{ settingState.changeUseShareSheetWhenSharedDeck(isChecked) }
                R.id.settingBase_switch_showMovesMsgInDeckEdit ->{ settingState.changeShowMovesMsgInDeckEdit(isChecked) }
                R.id.settingBase_switch_autoSaveDeck -> { settingState.changeAutoSaveDeck(isChecked) }
                R.id.settingBase_switch_showMoreMoveCEInfo ->{ settingState.changeShowMoreMoveCEInfo(isChecked) }
//                R.id.settingBase_switch_useNightMode ->{ settingState.changeUseNightMode(isChecked) }
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
        val view :View = inflater.inflate(R.layout.fragment_setting_base,container,false)

        baseBinding = FragmentSettingBaseBinding.bind(view)
        baseBinding?.lifecycleOwner = viewLifecycleOwner

        baseBinding?.apply {
            ViewCompat.setTransitionName(settingBaseTitle,"BaseTitle")

            settingBaseSwitchIsUseToolbar.setOnCheckedChangeListener(onSwitchCheckedChange)
            settingBaseSwitchShowMovesMsgInDeckEdit.setOnCheckedChangeListener(onSwitchCheckedChange)
            settingBaseSwitchAutoSaveDeck.setOnCheckedChangeListener(onSwitchCheckedChange)
            settingBaseChipGroupWhatMsgInDeckEdit.setOnCheckedStateChangeListener{ group, checkedIds ->
                if (checkedIds.isEmpty())
                    return@setOnCheckedStateChangeListener
                val tempBtn = group.findViewById<Chip>(checkedIds[0])
                if (!tempBtn.isPressed) {
                    return@setOnCheckedStateChangeListener
                }
                when(checkedIds[0]){
                    R.id.settingBase_chip_frameMsg -> settingState.changeShowWhatMsgInDeck(0)
                    R.id.settingBase_chip_trendMsg -> settingState.changeShowWhatMsgInDeck(1)
                }
            }
            settingBaseLinearModSelect.setOnClickListener {
                modSelectPopup.show()
            }
            settingBaseSwitchShowMoreMoveCEInfo.setOnCheckedChangeListener(onSwitchCheckedChange)

            settingBaseLinearNightModSelect.setOnClickListener {
                nightModSelectPopup.show()
            }

            ViewCompat.setTransitionName(settingBaseAdvance,"AdvanceTitle")
            settingBaseAdvance.setOnClickListener {
                val extra  = FragmentNavigatorExtras(settingBaseAdvance to "AdvanceTitle")
                nav().navigate(
                    R.id.action_settingBaseFragment_to_settingAdvanceFragment,
                    null,null,
                    extra
                )
            }

            if (requireContext().isNightMode()){
                settingBaseChipDefault.setTextColor(requireContext().getColor(com.google.android.material.R.color.design_dark_default_color_primary))
                settingBaseChipRed.setTextColor(resources.getColor(R.color.red_md_theme_dark_primary))
                settingBaseChipGold.setTextColor(resources.getColor(R.color.gold_md_theme_dark_primary))
                settingBaseChipBlue.setTextColor(resources.getColor(R.color.blue_md_theme_dark_primary))
                settingBaseChipGreen.setTextColor(resources.getColor(R.color.green_md_theme_dark_primary))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                settingBaseChipDefault.isEnabled = false
                settingBaseChipWallpaper.isEnabled = false
                settingBaseChipRed.isEnabled = false
                settingBaseChipGold.isEnabled = false
                settingBaseChipBlue.isEnabled = false
                settingBaseChipGreen.isEnabled = false
                settingState.changeUseWhatTheme(UseTheme.DefaultId)
            }
            settingBaseChipGroupTheme.setOnCheckedStateChangeListener { group, checkedIds ->// 由于我是singleSelect，所以这个ids只有一个
                if (checkedIds.isEmpty())
                    return@setOnCheckedStateChangeListener
                val tempBtn = group.findViewById<Chip>(checkedIds[0])
                if (!tempBtn.isPressed) {
                    return@setOnCheckedStateChangeListener
                }
                when(checkedIds[0]){
                    R.id.settingBase_chip_default ->{
                        settingState.changeUseWhatTheme(UseTheme.DefaultId)
                    }
                    R.id.settingBase_chip_wallpaper ->{
                        settingState.changeUseWhatTheme(UseTheme.WallpaperId)
                    }
                    R.id.settingBase_chip_red ->{
                        settingState.changeUseWhatTheme(UseTheme.RedId)
                    }
                    R.id.settingBase_chip_gold ->{
                        settingState.changeUseWhatTheme(UseTheme.YellowId)
                    }
                    R.id.settingBase_chip_blue ->{
                        settingState.changeUseWhatTheme(UseTheme.BlueId)
                    }
                    R.id.settingBase_chip_green ->{
                        settingState.changeUseWhatTheme(UseTheme.GreenId)
                    }
                }
                lifecycleScope.launchWhenStarted {
                    settingBaseChipDefault.isEnabled = false
                    settingBaseChipWallpaper.isEnabled = false
                    settingBaseChipRed.isEnabled = false
                    settingBaseChipGold.isEnabled = false
                    settingBaseChipBlue.isEnabled = false
                    settingBaseChipGreen.isEnabled = false
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
                    baseBinding?.settingBaseSwitchIsUseToolbar?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showMovesMsgInDeckEditFlow.collectLatest {
                    llog.i(TAG,"showMovesMsgInDeckEditFlow: 接收到数据 $it")
                    baseBinding?.settingBaseSwitchShowMovesMsgInDeckEdit?.isChecked = it
                    baseBinding?.settingBaseChipGroupWhatMsgInDeckEdit?.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showWhatMsgInDeckEditFlow.collectLatest {
                    llog.i(TAG,"showWhatMsgInDeckEditFlow: 接收到数据 $it")
                    baseBinding?.apply {
                        when(it){
                            0 -> settingBaseChipFrameMsg.isChecked = true
                            1 -> settingBaseChipTrendMsg.isChecked = true
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.autoSaveDeckWhenExitDeckEditFlow.collectLatest {
                    llog.i(TAG,"autoSaveDeckWhenExitDeckEditFlow: 接收到数据 $it")
                    baseBinding?.settingBaseSwitchAutoSaveDeck?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useWhatDataModFlow.collectLatest {
                    llog.i(TAG,"使用数据模式 -> $it")
                    baseBinding?.apply {
                        settingBaseTextMod.text = when(it){
                            SettingRepository.ORIGIN -> getString(R.string.origin_mod)
                            SettingRepository.CEMOD -> getString(R.string.ce_mod)
                            else -> getString(R.string.origin_mod)
                        }
                        settingBaseSwitchShowMoreMoveCEInfo.visibility = if (it == SettingRepository.CEMOD) View.VISIBLE else View.GONE
                        if (it != SettingRepository.CEMOD) settingState.changeShowMoreMoveCEInfo(false)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showMoreMoveCEInfoFlow.collectLatest {
                    llog.i(TAG, "showMoreMoveCEInfoFlow: 接收到数据 $it")
                    baseBinding?.settingBaseSwitchShowMoreMoveCEInfo?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useNightModeFlow.collectLatest {
                    llog.i(TAG, "useNightModeFlow: 接收到数据 $it")
                    baseBinding?.settingBaseTextNightMod?.apply {
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
                        baseBinding?.settingBaseChipGroupTheme?.check(R.id.settingBase_chip_default)
                        return@collectLatest
                    }
                    baseBinding?.apply {
                        when(it){
                            UseTheme.DefaultId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_default) }
                            UseTheme.WallpaperId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_wallpaper) }
                            UseTheme.RedId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_red) }
                            UseTheme.YellowId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_gold) }
                            UseTheme.BlueId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_blue) }
                            UseTheme.GreenId ->{ settingBaseChipGroupTheme.check(R.id.settingBase_chip_green) }
                        }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        baseBinding = null
    }
}