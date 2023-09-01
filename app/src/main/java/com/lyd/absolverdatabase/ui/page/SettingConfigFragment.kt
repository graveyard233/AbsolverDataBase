package com.lyd.absolverdatabase.ui.page

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.UseTheme
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingConfigBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
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
            settingConfigSwitchUseCNEditionMod.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseCNEditionMod(isChecked)
            }
            settingConfigSwitchShowMoreMoveCEInfo.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeShowMoreMoveCEInfo(isChecked)
            }
            settingConfigSwitchUseNightMode.setOnCheckedChangeListener{ btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseNightMode(isChecked)
            }

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
                settingState.askBeforeImportDeckFlow.collectLatest {
                    llog.i(TAG, "askBeforeImportDeckFlow: flow->$it")
                    configBinding?.settingConfigSwitchAskBeforeImport?.isChecked = it
//                    Log.i(TAG, "askBeforeImportDeckFlow: ${SettingRepository.isNeedAskBeforeImportPreference.getOrDefault()}")
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
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.dialogGaussianBlurFlow.collectLatest {
                    llog.i(TAG, "dialogGaussianBlurFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchGaussianBlur?.isChecked = it
                }
            }

        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useCNEditionModFlow.collectLatest {
                    llog.i(TAG, "useCNEditionModFlow: 接收到数据 $it")
                    configBinding?.apply {
                        settingConfigSwitchUseCNEditionMod.isChecked = it
                        settingConfigSwitchShowMoreMoveCEInfo.visibility = if (it) View.VISIBLE else View.GONE
                        if (!it) settingState.changeShowMoreMoveCEInfo(false)
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