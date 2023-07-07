package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingDatabaseState
import com.lyd.absolverdatabase.bridge.state.SettingDatabaseViewModelFactory
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingDatabaseBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.BaseDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingDatabaseFragment :BaseFragment() {

    private var setDbBinding :FragmentSettingDatabaseBinding ?= null
    private val settingState : SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })
    private val settingDatabaseState : SettingDatabaseState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingDatabaseViewModelFactory((mActivity?.application as App).settingDatabaseRepository)
    })
    private abstract class OnAntiShakeClickListener :View.OnClickListener{
        val shakeTime = 1000L
        var lastClick = 0L
        abstract fun antiShakeClick(view: View?)
        abstract fun whenShake()
        override fun onClick(p0: View?) {
            if (System.currentTimeMillis() - lastClick < shakeTime && lastClick != 0L){
                // 拦截点击
                whenShake()
            } else {
                lastClick = System.currentTimeMillis()
                antiShakeClick(p0)
            }
        }
    }
    private val snackBarWhenShakeClick by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Snackbar.make(setDbBinding!!.settingDatabaseRoot,R.string.shake_click, Snackbar.LENGTH_SHORT)
    }
    private val syncFromCloudDialog by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        BaseDialogBuilder(requireActivity())
            .setTitle("云端同步")
            .setIcon(R.drawable.ic_sync_from_cloud)
            .setMessage("同步数据中")
            .setView(LinearProgressIndicator(requireContext()).apply { isIndeterminate = true })
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view :View = inflater.inflate(R.layout.fragment_setting_database,container,false)

        setDbBinding = FragmentSettingDatabaseBinding.bind(view)
        setDbBinding?.lifecycleOwner = viewLifecycleOwner

        setDbBinding?.apply {
            settingDatabaseSyncOriginFromLocal.setOnClickListener(object : OnAntiShakeClickListener() {
                override fun antiShakeClick(view: View?) {
                    settingDatabaseState.syncOriginTable(whenFinish = {
                        Snackbar.make(settingDatabaseRoot,R.string.sync_finish,Snackbar.LENGTH_SHORT).show()
                    })
                }
                override fun whenShake() {
                    snackBarWhenShakeClick.show()
                }
            })
            settingDatabaseSyncCEFromCloud.setOnClickListener(object : OnAntiShakeClickListener(){
                override fun antiShakeClick(view: View?) {
                    syncFromCloudDialog.show()
                    settingDatabaseState.syncCETableFromCloud(
                        whenError = {
                            syncFromCloudDialog.dismiss()
                            showShortToast("同步失败:$it")
                        },
                        whenSuccess = {
                            syncFromCloudDialog.dismiss()
                            Log.i(TAG, "antiShakeClick: success $it")
                            showShortToast("同步成功,耗时:${it}秒")
                        }
                    )
                }
                override fun whenShake() {
                    snackBarWhenShakeClick.show()
                }
            })
            settingDatabaseSyncCEFromLocal.setOnClickListener(object : OnAntiShakeClickListener(){
                override fun antiShakeClick(view: View?) {
                    settingDatabaseState.syncCETable(whenFinish ={
                        Snackbar.make(settingDatabaseRoot,R.string.sync_finish,Snackbar.LENGTH_SHORT).show()
                    })
                }
                override fun whenShake() {
                    snackBarWhenShakeClick.show()
                }
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingState.useCNEditionModFlow.collectLatest {
                    Log.i(TAG, "useCNEditionModFlow: 接收到数据 $it")
                    setDbBinding?.settingDatabaseDatabaseLinear?.visibility = if (it) View.VISIBLE else View.GONE
                 }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        setDbBinding = null
    }
}