package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.utils.dataStore.DataStoreOwner
import com.lyd.absolverdatabase.utils.logUtils.LLog

object SettingRepository :DataStoreOwner(name = "setting") {
    private const val TAG = "SettingRepository"

    var isDialogGaussianBlur :Boolean = true
    val isDialogGaussianBlurPreference by booleanPreference(default = true)

    var isNeedAskBeforeImport :Boolean = true
    val isNeedAskBeforeImportPreference by booleanPreference(default = true)

    var isUseCNEditionMod :Boolean = false
    val isUseCNEditionModPreference by booleanPreference(default = false)
    var isShowMoreMoveCEInfo :Boolean = false
    val isShowMoreMoveCEInfoPreference by booleanPreference(default = false)

    /**true:启用使用夜间 false:跟随系统*/
    var useNightMode :Boolean = false
    val useNightModePreference by booleanPreference(default = false)

    var useWhatTheme :Int = 1
    val useWhatThemePreference by intPreference(default = 1)

    /**提示如何编辑卡组的详细信息*/
    var hadShowTipHowToEditDeckMsg :Boolean = true
    val hadShowTipHowToEditDeckMsgPreference by booleanPreference(default = true)

    /**分享卡组时是否增加详细信息*/
    var isShowSeqDetailWhenSharedDeck :Boolean = false
    val isShowSeqDetailWhenSharedDeckPreference by booleanPreference(default = false)

    /**使用Android原生分享控件*/
    var isUseShareSheetWhenSharedDeck :Boolean = true
    val isUseShareSheetWhenSharedDeckPreference by booleanPreference(default = true)

    /**崩溃时是否记录崩溃信息*/
    var isRecordCrashMsg :Boolean = false
    val isRecordCrashMsgPreference by booleanPreference(default = true)

    /**[LLog]的日志打印等级*/
    var logPrintLevel :Int = 36
        set(value) {
            LLog.minPrintPriority = value / 10
            LLog.maxPrintPriority = value % 10
            field = value
        }
    val logPrintLevelPreference by intPreference(default = 36)

    /**[LLog]的日志写入等级*/
    var logWriteLevel :Int = 46
        set(value) {
            LLog.minWritePriority = value / 10
            LLog.maxWritePriority = value % 10
            field = value
        }
    val logWriteLevelPreference by intPreference(default = 46)

}