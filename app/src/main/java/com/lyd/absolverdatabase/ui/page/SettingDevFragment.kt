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
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.utils.crashUtils.CrashHelperUtil
import com.lyd.architecture.utils.FileUtils

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FilenameFilter

class SettingDevFragment :BaseFragment() {

    private val settingState :SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private lateinit var switchRecordCrashMsg :MaterialSwitch
    private var textCrashDirPath :TextView ?= null
    private var textCrashFilesMsg :TextView ?= null
    private lateinit var btnCleanCrashFiles :MaterialButton
    private lateinit var sliderLogPrintLevel :RangeSlider
    private lateinit var sliderLogWriteLevel :RangeSlider
    private var textLogDirPath :TextView ?= null
    private var textLogFilesMsg :TextView ?= null
    private lateinit var btnCleanLogFiles :MaterialButton

    private val logLevelLabelFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        LabelFormatter { value -> llog.getLevelByInt(value) }
    }

    private val snackBarDeleteFilesFalse by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Snackbar.make(btnCleanLogFiles,getString(R.string.clear_files_false),Snackbar.LENGTH_SHORT)
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
            textCrashDirPath = findViewById(R.id.setting_dev_text_crashDirPath)
            textCrashFilesMsg = findViewById(R.id.setting_dev_text_crashFilesMsg)
            btnCleanCrashFiles = findViewById(R.id.setting_dev_btn_cleanCrashFiles)

            textCrashDirPath?.text = requireContext().getExternalFilesDir("crash")!!.absolutePath
            btnCleanCrashFiles.setOnClickListener {
                if (FileUtils.deleteAllInDir(requireContext().getExternalFilesDir("crash")!!)){
                    initCrashFilesMsg()
                } else {
                    llog.w(TAG,"删除崩溃日志失败")
                    snackBarDeleteFilesFalse.show()
                }
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
            textLogDirPath = findViewById(R.id.setting_dev_text_logDirPath)
            textLogFilesMsg = findViewById(R.id.setting_dev_text_logFilesMsg)
            btnCleanLogFiles = findViewById(R.id.setting_dev_btn_cleanLogFiles)


            textLogDirPath?.text = requireContext().getExternalFilesDir("crash")!!.absolutePath
            btnCleanLogFiles.setOnClickListener {
                if (FileUtils.deleteAllInDir(requireContext().getExternalFilesDir("log")!!.absolutePath)){
                    initLogFilesMsg()
                } else {
                    llog.w(TAG,"删除日志失败")
                    snackBarDeleteFilesFalse.show()
                }
            }

            initCrashFilesMsg()

            initLogFilesMsg()

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.recordCrashMsgFlow.collectLatest {
                    switchRecordCrashMsg.isChecked = it
                    llog.i(msg = "崩溃时是否记录崩溃信息:${SettingRepository.isRecordCrashMsg}")
                }
            }
        }

        // 这里的rangSlider每次应用启动的第一次进来，都会触发两次，第一次是预设值，第二次就是我们配置的值
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.logPrintLevelFlow.collectLatest {
                    sliderLogPrintLevel.values = it.toRangeList()
                    llog.i(msg = "llog日志打印级别是:${llog.getLevelByInt(it.toFloat() / 10)} ~ ${llog.getLevelByInt(it.toFloat() % 10)}")
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.logWriteLevelFlow.collectLatest {
                    sliderLogWriteLevel.values = it.toRangeList()
                    llog.i(msg = "llog日志写入级别是:${llog.getLevelByInt(it.toFloat() / 10)} ~ ${llog.getLevelByInt(it.toFloat() % 10)}")
                }
            }
        }

    }

    private fun initCrashFilesMsg(){
        val crashDir = requireContext().getExternalFilesDir("crash")!!
        val crashLogNum = crashDir.listFiles()?.size ?: 0
        textCrashFilesMsg?.text = getString(R.string.crash_files_msg, crashLogNum, crashLogNum.let {
            if (it == 0){
                "null"
            } else {
                FileUtils.getDirSize(crashDir)
            }
        })
    }

    private fun initLogFilesMsg(){
        val logDir = requireContext().getExternalFilesDir("log")!!
        val logNum = logDir.listFiles()?.size ?: 0
        textLogFilesMsg?.text = getString(R.string.log_files_msg, logNum, FileUtils.getDirSize(logDir).let {
            if (logNum == 0){
                "null"
            } else {
                it
            }
        })
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