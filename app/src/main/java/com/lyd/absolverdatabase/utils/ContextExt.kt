package com.lyd.absolverdatabase.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Display
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.getSystemService
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import kotlin.math.roundToInt

/**
 * Returns true if current context is in night mode
 */
fun Context.isNightMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


val Context.displayCompat: Display?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display
    } else {
        @Suppress("DEPRECATION")
        getSystemService<WindowManager>()?.defaultDisplay
    }


/**
 * Returns the color for the given attribute.
 *
 * @param resource the attribute.
 * @param alphaFactor the alpha number [0,1].
 */
@ColorInt
fun Context.getResourceColor(@AttrRes resource: Int, alphaFactor: Float = 1f): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(resource))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()

    if (alphaFactor < 1f) {
        val alpha = (color.alpha * alphaFactor).roundToInt()
        return Color.argb(alpha, color.red, color.green, color.blue)
    }

    return color
}

fun Context.restartApp(){
    packageManager.getLaunchIntentForPackage(packageName)?.let {
        return@let Intent.makeRestartActivityTask(it.component)
    }?.let {
        startActivity(it)
    }
    android.os.Process.killProcess(android.os.Process.myPid())
}

fun Context.vibrate(){
    if (SettingRepository.isUseVibrate){
        val time = SettingRepository.vibrateParams / 1000
        val strength = SettingRepository.vibrateParams % 1000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val v = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            if (v.defaultVibrator.hasVibrator()){
                v.defaultVibrator.vibrate(VibrationEffect.createOneShot(time.toLong(),strength))
            }
        } else {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (v.hasVibrator()){
                v.vibrate(VibrationEffect.createOneShot(time.toLong(),strength))
            }
        }
    }
}