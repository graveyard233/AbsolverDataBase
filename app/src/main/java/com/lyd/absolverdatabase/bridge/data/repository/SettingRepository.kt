package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.utils.dataStore.DataStoreOwner

object SettingRepository :DataStoreOwner(name = "setting") {
    private const val TAG = "SettingRepository"

    var isDialogGaussianBlur :Boolean = true
    val isDialogGaussianBlurPreference by booleanPreference(default = true)

    var isNeedAskBeforeImport :Boolean = true
    val isNeedAskBeforeImportPreference by booleanPreference(default = true)

    var isUseCNEditionMod :Boolean = false
    val isUseCNEditionModPreference by booleanPreference(default = false)

    /**true:启用使用夜间 false:跟随系统*/
    var useNightMode :Boolean = false
    val useNightModePreference by booleanPreference(default = false)

    var useWhatTheme :Int = 1
    val useWhatThemePreference by intPreference(default = 1)
}