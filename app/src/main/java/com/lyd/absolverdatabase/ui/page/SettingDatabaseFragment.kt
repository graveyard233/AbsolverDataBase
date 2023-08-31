package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
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
import com.lyd.absolverdatabase.utils.TimeUtils
import com.lyd.absolverdatabase.utils.logUtils.LLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.apache.commons.text.StringEscapeUtils

class SettingDatabaseFragment :BaseFragment() {

    companion object{
        private const val getHtml :String = "javascript:function getHtmlCode(){" +
                "var content = document.getElementsByTagName('html')[0].innerHTML;" +
                "return '<html>' + content + '</html>'" +
                "}"
    }

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
            .setTitle(getString(R.string.sync_with_webView))
            .setIcon(R.drawable.ic_sync_from_cloud)
            .setMessage(getString(R.string.on_sync_ing_data))
            .setView(LinearProgressIndicator(requireContext()).apply { isIndeterminate = true })
            .create()
    }

    private var agentWeb :AgentWeb ?= null
    private var startTime = 0L
    private var checkTimeFirst = 0
    private val webViewClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        object :WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 在这里注入JS，但还没调用
                view?.evaluateJavascript(SettingDatabaseFragment.getHtml) {

                }
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request?.method == "POST"){
                    Log.i(TAG, "shouldInterceptRequest: ${request.url}")
                    if (request.url.toString().startsWith("https://ev.csdn.net")){
                        checkTimeFirst++
                        if (checkTimeFirst == 2){
                            LLog.i(TAG, "shouldInterceptRequest: 可以拿HTML代码了")
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(1000)
                                agentWeb?.jsAccessEntrace?.quickCallJs("getHtmlCode",
                                    { value ->
                                        if (!value.isNullOrEmpty()){
                                            val trueHtml = StringEscapeUtils.unescapeEcmaScript(value)
                                            settingDatabaseState.syncCETableFromWebView(html = trueHtml, startTime = startTime,
                                            whenError = {
                                                Snackbar.make(setDbBinding!!.settingDatabaseRoot,"${getString(R.string.sync_error)}:$it",Snackbar.LENGTH_SHORT).show()
                                            },
                                            whenSuccess = {
                                                Snackbar.make(setDbBinding!!.settingDatabaseRoot,getString(R.string.sync_success_and_cost_time,it.toString()),Snackbar.LENGTH_SHORT).show()
                                            },
                                            whenFinish = {
                                                syncFromCloudDialog.dismiss()
                                                agentWeb?.webLifeCycle?.onDestroy()// 释放webView
                                                setDbBinding?.settingDatabaseWebLinear?.visibility = View.GONE// 把webView藏起来
                                            })
                                        } else {
                                            Snackbar.make(setDbBinding!!.settingDatabaseRoot,getString(R.string.html_data_is_null),Snackbar.LENGTH_SHORT).show()
                                            syncFromCloudDialog.dismiss()
                                        }
                                    })
                            }
                            checkTimeFirst = 0
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
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
        val view :View = inflater.inflate(R.layout.fragment_setting_database,container,false)

        setDbBinding = FragmentSettingDatabaseBinding.bind(view)
        setDbBinding?.lifecycleOwner = viewLifecycleOwner

        setDbBinding?.apply {
            ViewCompat.setTransitionName(settingDatabaseDatabaseTitle,"DatabaseTitle")
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
                    lifecycleScope.launchWhenStarted {
                        settingDatabaseWebLinear.visibility = View.VISIBLE
                        syncFromCloudDialog.show()

                        startTime = TimeUtils.curTime
                        initWebView()

                    }

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
                    LLog.i(TAG, "useCNEditionModFlow: 接收到数据 $it")
                    setDbBinding?.settingDatabaseDatabaseLinear?.visibility = if (it) View.VISIBLE else View.GONE
                 }
            }
        }


    }

    private fun initWebView(){
        setDbBinding?.apply {
            LLog.i(TAG, "initWebView 加载url")
            agentWeb = AgentWeb.with(this@SettingDatabaseFragment)
                .setAgentWebParent(settingDatabaseWebLinear, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .setWebViewClient(webViewClient)
                .createAgentWeb()
                .ready()
                .go("https://blog.csdn.net/graveyard233/article/details/131581015")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        setDbBinding = null
    }
}