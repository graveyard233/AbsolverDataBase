package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.logUtils.LLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingDevFragment :BaseFragment() {

    private val settingState :SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private lateinit var switchRecordCrashMsg :MaterialSwitch
    private lateinit var sliderLogLevel :Slider

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
        val view :View = inflater.inflate(R.layout.fragment_dev,container,false)

        view.apply {
            ViewCompat.setTransitionName(view.findViewById<TextView>(R.id.setting_dev_title),"DevTitle")

            switchRecordCrashMsg = findViewById(R.id.setting_dev_switch_saveCrashFile)
            switchRecordCrashMsg.setOnCheckedChangeListener{ btn, isCheck ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeRecordCrashMsg(isCheck)
            }

            sliderLogLevel = findViewById(R.id.setting_dev_slider_logLevel)
            sliderLogLevel.apply {

                setLabelFormatter { value:Float ->
                    return@setLabelFormatter when(value.toInt()){
                        Log.VERBOSE -> "V"
                        Log.DEBUG -> "DEBUG"
                        Log.INFO -> "INFO"
                        Log.WARN -> "WARN"
                        Log.ERROR -> "ERROR"

                        LLog.NONE -> "NONE"
                        else -> "something wrong"
                    }
                }
                addOnChangeListener{ _,value,fromUser->
                    if (!fromUser) return@addOnChangeListener
                    settingState.changeLogPrintLevel(value.toInt())
                }
            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.recordCrashMsgFlow.collectLatest {
                    switchRecordCrashMsg.isChecked = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.logPrintLevelFlow.collectLatest {
                    sliderLogLevel.value = it.toFloat()
                }
            }
        }

    }
}