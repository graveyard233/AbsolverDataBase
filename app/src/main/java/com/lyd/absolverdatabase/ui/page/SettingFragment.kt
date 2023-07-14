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
import androidx.navigation.navGraphViewModels
import com.lyd.absolverdatabase.BuildConfig
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class SettingFragment : BaseFragment() {

    private var settingBinding : FragmentSettingBinding? = null
    private val settingState : SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

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

            settingTitleConfig.setOnClickListener {
                nav().navigate(SettingFragmentDirections.actionSettingFragmentToSettingConfigFragment())
            }
            settingTitleDatabase.setOnClickListener {
                nav().navigate(SettingFragmentDirections.actionSettingFragmentToSettingDatabaseFragment())
            }
            settingItemThanks.setOnClickListener {
                nav().navigate(SettingFragmentDirections.actionSettingFragmentToSettingLicenseFragment())
            }

            settingAboutVersion.text = getString(R.string.version,BuildConfig.VERSION_NAME)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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