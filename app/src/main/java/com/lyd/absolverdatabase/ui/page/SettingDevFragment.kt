package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
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
import com.google.android.material.slider.LabelFormatter
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
    private lateinit var sliderLogPrintLevel :RangeSlider
    private lateinit var sliderLogWriteLevel :RangeSlider

    private val logLevelLabelFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        LabelFormatter { value -> LLog.getLevelByInt(value) }
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
        val view :View = inflater.inflate(R.layout.fragment_dev,container,false)

        view.apply {
            ViewCompat.setTransitionName(view.findViewById<TextView>(R.id.setting_dev_title),"DevTitle")

            switchRecordCrashMsg = findViewById(R.id.setting_dev_switch_saveCrashFile)
            switchRecordCrashMsg.setOnCheckedChangeListener{ btn, isCheck ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeRecordCrashMsg(isCheck)
            }

            sliderLogPrintLevel = findViewById(R.id.setting_dev_slider_logPrintLevel)
            sliderLogPrintLevel.apply {
                setLabelFormatter(logLevelLabelFormat)
                addOnChangeListener{ slider, _, fromUser->
                    if (!fromUser) return@addOnChangeListener
                    settingState.changeLogPrintLevel(slider.values.toRange().toInt())
                }
            }

            sliderLogWriteLevel = findViewById(R.id.setting_dev_slider_logWriteLevel)
            sliderLogWriteLevel.apply {
                setLabelFormatter(logLevelLabelFormat)
                addOnChangeListener { slider, _, fromUser ->
                    if (!fromUser) return@addOnChangeListener
                    settingState.changeLogWriteLevel(slider.values.toRange().toInt())
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
                    LLog.i(msg = "崩溃时是否记录崩溃信息:${SettingRepository.isRecordCrashMsg}")
                }
            }
        }

        // 这里的rangSlider每次应用启动的第一次进来，都会触发两次，第一次是预设值，第二次就是我们配置的值
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.logPrintLevelFlow.collectLatest {
                    sliderLogPrintLevel.values = it.toRangeList()
                    LLog.i(msg = "LLog日志打印级别是:${LLog.getLevelByInt(it.toFloat() / 10)} ~ ${LLog.getLevelByInt(it.toFloat() % 10)}")
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.logWriteLevelFlow.collectLatest {
                    sliderLogWriteLevel.values = it.toRangeList()
                    LLog.i(msg = "LLog日志写入级别是:${LLog.getLevelByInt(it.toFloat() / 10)} ~ ${LLog.getLevelByInt(it.toFloat() % 10)}")
                }
            }
        }

    }

    private fun List<Float>.toRange() :Float{
        return this[0].toInt() * 10 + this[1]
    }

    private fun Int.toRangeList() :List<Float>{
        return listOf(
            (this / 10).toFloat(),
            (this % 10).toFloat()
        )
    }
}