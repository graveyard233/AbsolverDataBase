package com.lyd.absolverdatabase.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lyd.absolverdatabase.BuildConfig
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingFragment : BaseFragment() {

    private var settingBinding : FragmentSettingBinding? = null
    private val settingState : SettingState by viewModels {
        SettingViewModelFactory(SettingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        settingState = getFragmentViewModelProvider(this)[SettingState::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_setting,container,false)

        settingBinding = FragmentSettingBinding.bind(view)
        settingBinding?.vm = settingState
        settingBinding?.click = ClickProxy()
        settingBinding?.lifecycleOwner = viewLifecycleOwner

        settingBinding?.apply {

            settingAboutVersion.text = getString(R.string.version,BuildConfig.VERSION_NAME)
            settingSwitchGaussianBlur.apply {
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
            settingSwitchAskBeforeImport.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeAskBeforeImportDeck(isChecked)
            }
            settingSwitchShowStyleIconInMoveMsg.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeShowStyleIconInMoveMsg(isChecked)
            }
            settingSwitchUseCNEditionMod.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                settingState.changeUseCNEditionMod(isChecked)
            }

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 在这里进行liveData的监听
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.askBeforeImportDeckFlow.collectLatest {
                    Log.i(TAG, "askBeforeImportDeckFlow: flow->$it isChecked->${settingBinding?.settingSwitchAskBeforeImport?.isChecked}")
                    settingBinding?.settingSwitchAskBeforeImport?.isChecked = it
//                    Log.i(TAG, "askBeforeImportDeckFlow: ${SettingRepository.isNeedAskBeforeImportPreference.getOrDefault()}")
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.dialogGaussianBlurFlow.collectLatest {
                    Log.i(TAG, "dialogGaussianBlurFlow: 接收到数据 $it")
                    settingBinding?.settingSwitchGaussianBlur?.isChecked = it
                }
            }

        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.showStyleIconInMoveMsgFlow.collectLatest {
                    Log.i(TAG, "showStyleIconInMoveMsg: 接收到数据 $it")
                    settingBinding?.settingSwitchShowStyleIconInMoveMsg?.isChecked = it
                }
            }
        }

        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useCNEditionModFlow.collectLatest {
                    Log.i(TAG, "useGPModFlow: 接收到数据 $it")
                    settingBinding?.settingSwitchUseCNEditionMod?.isChecked = it
                }
            }
        }
    }

    inner class ClickProxy {
        fun openWeb(textView: View){
            val temp = textView as TextView
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    temp.hint.toString().let {
                        Log.i(TAG, "openWeb: $it")
                        Uri.parse(it)
                    }
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        settingBinding = null
    }
}