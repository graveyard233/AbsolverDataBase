package com.lyd.absolverdatabase.ui.page

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            settingConfigSwitchUseCNEditionMod.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseCNEditionMod(isChecked)
            }
            settingConfigSwitchUseNightMode.setOnCheckedChangeListener{ btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseNightMode(isChecked)
            }
            settingConfigSlider.addOnChangeListener { slider, value, fromUser ->
                if (!fromUser) return@addOnChangeListener
                settingState.changeUseWhatTheme(value.toInt())
                Log.i(TAG, "settingConfigSlider in View: changeUseWhatTheme 选择了$value")
                lifecycleScope.launchWhenStarted {
                    // TODO: 逻辑还能再优化，比如锁定等 或者重写slider
                    showShortToast("一秒后会重启应用")
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
                    Log.i(TAG, "askBeforeImportDeckFlow: flow->$it isChecked->${configBinding?.settingConfigSwitchAskBeforeImport?.isChecked}")
                    configBinding?.settingConfigSwitchAskBeforeImport?.isChecked = it
//                    Log.i(TAG, "askBeforeImportDeckFlow: ${SettingRepository.isNeedAskBeforeImportPreference.getOrDefault()}")
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.dialogGaussianBlurFlow.collectLatest {
                    Log.i(TAG, "dialogGaussianBlurFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchGaussianBlur?.isChecked = it
                }
            }

        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useCNEditionModFlow.collectLatest {
                    Log.i(TAG, "useGPModFlow: 接收到数据 $it")
                    configBinding?.settingConfigSwitchUseCNEditionMod?.isChecked = it
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useNightModeFlow.collectLatest {
                    Log.i(TAG, "useNightModeFlow: 接收到数据 $it")
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
                    Log.i(TAG, "useWhatThemeFlow: 接收到数据 $it")
                    configBinding?.apply {
                        settingConfigSlider.setValues(it.toFloat())
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