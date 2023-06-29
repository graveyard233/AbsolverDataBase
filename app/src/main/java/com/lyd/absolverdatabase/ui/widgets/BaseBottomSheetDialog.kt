package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.displayCompat
import com.lyd.absolverdatabase.utils.isNightMode
import com.lyd.absolverdatabase.utils.setNavigationBarTransparentCompat

abstract class BaseBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    abstract fun createView(inflater: LayoutInflater): View

    @RequiresApi(Build.VERSION_CODES.R)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootView = createView(layoutInflater)
        setContentView(rootView)

        // Enforce max width for tablets
        val width = context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_width)
        if (width > 0) {
            behavior.maxWidth = width
        }

        // Set peek height to 50% display height
        context.displayCompat?.let {
            val metrics = DisplayMetrics()
            it.getRealMetrics(metrics)
            behavior.peekHeight = metrics.heightPixels / 2
        }

        // 这一段不知道是干啥的，貌似是兼容低版本的
//        // Set navbar color to transparent for edge-to-edge bottom sheet if we can use light navigation bar
//        // TODO Replace deprecated systemUiVisibility when material-components uses new API to modify status bar icons
//        // window?.setNavigationBarTransparentCompat(context, behavior.getElevation())
//        window?.setNavigationBarTransparentCompat(context, 0f)
//        val bottomSheet = rootView.parent as ViewGroup
//        var flags = bottomSheet.systemUiVisibility
//        flags = if (context.isNightMode()) {
//            flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
//        } else {
//            flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//        }
//        bottomSheet.systemUiVisibility = flags

        // 高斯模糊
        window?.let {
            if (Build.VERSION.SDK_INT >= 31){
                if (SettingRepository.isDialogGaussianBlur){
                    it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.attributes.blurBehindRadius = 32
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //拿到系统的 bottom_sheet，然后设置高度
        val view: FrameLayout = this.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }
}