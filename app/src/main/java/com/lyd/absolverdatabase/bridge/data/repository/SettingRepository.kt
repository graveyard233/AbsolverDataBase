package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.utils.dataStore.DataStoreOwner

object SettingRepository :DataStoreOwner(name = "setting") {
    private const val TAG = "SettingRepository"

    var isDialogGaussianBlur :Boolean = true
    val isDialogGaussianBlurPreference by booleanPreference(default = true)

    var isNeedAskBeforeImport :Boolean = true
    val isNeedAskBeforeImportPreference by booleanPreference(default = true)

    var isNeedShowStyleIconInMoveMsg :Boolean = false
    val isNeedShowStyleIconInMoveMsgPreference by booleanPreference(default = false)

    var isUseCNEditionMod :Boolean = false
    val isUseCNEditionModPreference by booleanPreference(default = false)

}