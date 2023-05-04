package com.lyd.absolverdatabase.utils

import android.content.Context
import android.graphics.Color
import android.view.Window
import com.google.android.material.elevation.ElevationOverlayProvider

/**
 * Sets navigation bar color to transparent if system's config_navBarNeedsScrim is false,
 * otherwise it will use the theme navigationBarColor with 70% opacity.
 *
 * @see isNavigationBarNeedsScrim
 */
fun Window.setNavigationBarTransparentCompat(context: Context, elevation: Float = 0F) {
    navigationBarColor =
        ElevationOverlayProvider(context).compositeOverlayIfNeeded(
            context.getResourceColor(android.R.attr.navigationBarColor, 0.7F),
            elevation,
        )
}