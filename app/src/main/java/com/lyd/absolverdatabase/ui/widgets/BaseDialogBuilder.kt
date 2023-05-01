package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 在sdk31及以上会有磨砂特效的dialogBuilder
 * @param context 注意，这里要传Activity.this，传别的context报错，说找不到主题
 * */
open class BaseDialogBuilder constructor(
    context: Context
) : MaterialAlertDialogBuilder(context) {
    override fun create(): AlertDialog {
        return super.create().also { dialog ->
            dialog.window?.let {
                if (Build.VERSION.SDK_INT >= 31){
                    it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.attributes.blurBehindRadius = 32
                }
            }
        }
    }
}