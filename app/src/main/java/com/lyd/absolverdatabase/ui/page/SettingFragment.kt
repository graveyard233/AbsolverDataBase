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
import androidx.core.view.ViewCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.lyd.absolverdatabase.BuildConfig
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.bridge.state.SettingState
import com.lyd.absolverdatabase.bridge.state.SettingViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentSettingBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment
import com.lyd.absolverdatabase.ui.widgets.BaseDialogBuilder
import com.lyd.absolverdatabase.utils.ClipUtil

class SettingFragment : BaseFragment() {

    private var settingBinding : FragmentSettingBinding? = null
    private val settingState : SettingState by navGraphViewModels(navGraphId = R.id.nav_setting, factoryProducer = {
        SettingViewModelFactory(SettingRepository)
    })

    private val snackBarWithEmail by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        Snackbar.make(settingBinding!!.settingRoot,resources.getString(R.string.feedback_snackbar_content,"1991206268@qq.com"),Snackbar.LENGTH_LONG)
            .setAction(R.string.copy) {
                ClipUtil.copyText("1991206268@qq.com")
            }
    }

    private val dialogHowToUse by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        BaseDialogBuilder(requireContext())
            .setTitle(R.string.how_to_use_title)
            .setIcon(R.drawable.ic_about)
            .setCancelable(false)
            .setPositiveButton(R.string.how_to_use_positive){ dialog,_->
                dialog.dismiss()
            }
            .setMessage(R.string.how_to_use_content)
            .create()
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

            ViewCompat.setTransitionName(settingTitleConfig,"ConfigTitle")
            settingTitleConfig.setOnClickListener {
                val extra = FragmentNavigatorExtras(settingTitleConfig to "ConfigTitle")
                nav().navigate(
                    R.id.action_settingFragment_to_settingConfigFragment,
                    null,null,
                    extra
                )
            }
            ViewCompat.setTransitionName(settingTitleDatabase,"DatabaseTitle")
            settingTitleDatabase.setOnClickListener {
                val extra = FragmentNavigatorExtras(settingTitleDatabase to "DatabaseTitle")
                nav().navigate(/*SettingFragmentDirections.actionSettingFragmentToSettingDatabaseFragment(),*/
                    R.id.action_settingFragment_to_settingDatabaseFragment,
                    null,null,
//                    NavOptions.Builder().setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
//                        .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
//                        .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
//                        .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
//                        .build(),
                    navigatorExtras = extra)
            }
            settingTitleAbout.setOnClickListener { dialogHowToUse.show() }
            ViewCompat.setTransitionName(settingTitleDev,"DevTitle")
            settingTitleDev.setOnClickListener{
                val extra = FragmentNavigatorExtras(settingTitleDev to "DevTitle")
                nav().navigate(
                    R.id.action_settingFragment_to_settingDevFragment,
                    null, null,
                    navigatorExtras = extra
                )
//                nav().navigate(SettingFragmentDirections.actionSettingFragmentToSettingDevFragment())
            }
            settingItemThanks.setOnClickListener {
                nav().navigate(SettingFragmentDirections.actionSettingFragmentToSettingLicenseFragment())
            }

            settingAboutVersion.text = getString(R.string.version,BuildConfig.VERSION_NAME)

            settingItemFeedback.setOnClickListener {
                if (!snackBarWithEmail.isShown){
                    snackBarWithEmail.show()
                }
            }
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