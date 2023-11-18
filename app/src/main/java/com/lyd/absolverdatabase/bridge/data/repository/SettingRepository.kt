package com.lyd.absolverdatabase.bridge.data.repository

import com.lyd.absolverdatabase.bridge.data.bean.FilterItem
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.dataStore.DataStoreOwner
import com.lyd.absolverdatabase.ui.page.MoveRecycleFragment
import com.lyd.absolverdatabase.utils.logUtils.LLog

object SettingRepository :DataStoreOwner(name = "setting") {
    private const val TAG = "SettingRepository"
    const val ORIGIN = 0
    const val CEMOD = 1

    /*---------------------------可配置项页面部分，按序排列----------------------------------*/

    var isUseToolbar :Boolean = true
    val isUseToolbarPreference by booleanPreference(default = true)

    var isDialogGaussianBlur :Boolean = true
    val isDialogGaussianBlurPreference by booleanPreference(default = true)

    var isNeedAskBeforeImport :Boolean = true
    val isNeedAskBeforeImportPreference by booleanPreference(default = true)

    /**分享卡组时是否增加详细信息*/
    var isShowSeqDetailWhenSharedDeck :Boolean = false
    val isShowSeqDetailWhenSharedDeckPreference by booleanPreference(default = false)

    /**使用Android原生分享控件*/
    var isUseShareSheetWhenSharedDeck :Boolean = true
    val isUseShareSheetWhenSharedDeckPreference by booleanPreference(default = true)

    var isShowMovesMsgInDeckEdit :Boolean = false
    val isShowMovesMsgInDeckEditPreference by booleanPreference(default = false)

    /**0 显示帧数，1 显示走向*/
    var showWhatMsgInDeckEdit :Int = 1
    val showWhatMsgInDeckEditPreference by intPreference(default = 1)

    /**[ORIGIN] 是使用原版数据，[CEMOD] 是使用CEMod数据*/
    var useWhatDataMod :Int = ORIGIN
    val useWhatDataModPreference by intPreference(default = ORIGIN)
    var isShowMoreMoveCEInfo :Boolean = false
    val isShowMoreMoveCEInfoPreference by booleanPreference(default = false)

    /**true:启用使用夜间 false:跟随系统*/
    var useNightMode :Boolean = false
    val useNightModePreference by booleanPreference(default = false)

    /**用于排序筛选项的listJson文本*/
    var movesFilterListJson: String = GsonUtils.toJson(
        listOf(
            FilterItem(FilterItem.STRENGTH, isChecked = true, name = "Strength"),
            FilterItem(FilterItem.RANGE, isChecked = true, name = "Range"),
            FilterItem(FilterItem.EFFECT, isChecked = true, name = "Effect"),
            FilterItem(FilterItem.START_FRAME, isChecked = true, name = "StartFrame"),
            FilterItem(FilterItem.PHYSICAL_WEAKNESS, isChecked = true, name = "PhyWeakness"),
            FilterItem(FilterItem.PHYSICAL_OUTPUT, isChecked = true, name = "PhyOutput"),
            FilterItem(FilterItem.HIT_ADVANTAGE_FRAME, isChecked = true, name = "HitAdvantage"),
            FilterItem(FilterItem.DEF_ADVANTAGE_FRAME, isChecked = true, name = "DefAdvantage")
        )
    )
    val movesFilterListJsonPreference by stringPreference(
        default = GsonUtils.toJson(
            listOf(
                FilterItem(FilterItem.STRENGTH, isChecked = true, name = "Strength"),
                FilterItem(FilterItem.RANGE, isChecked = true, name = "Range"),
                FilterItem(FilterItem.EFFECT, isChecked = true, name = "Effect"),
                FilterItem(FilterItem.START_FRAME, isChecked = true, name = "StartFrame"),
                FilterItem(FilterItem.PHYSICAL_WEAKNESS, isChecked = true, name = "PhyWeakness"),
                FilterItem(FilterItem.PHYSICAL_OUTPUT, isChecked = true, name = "PhyOutput"),
                FilterItem(FilterItem.HIT_ADVANTAGE_FRAME, isChecked = true, name = "HitAdvantage"),
                FilterItem(FilterItem.DEF_ADVANTAGE_FRAME, isChecked = true, name = "DefAdvantage")
            )
        )
    )

    /**用于控制[MoveRecycleFragment.gridLayoutManager]的列数*/
    var moveItemsInOneRow :Int = 4
    val moveItemsInOneRowPreference by intPreference(default = 4)

    var useWhatTheme :Int = 1
    val useWhatThemePreference by intPreference(default = 1)

    /**用于控制已选择的招式的标记形式 0 是使用框框，1 是使用√*/
    var whichUsedMoveTag :Int = 1
    val whichUsedMoveTagPreference by intPreference(default = 1)

    var isUseVibrate :Boolean = true
    val isUseVibratePreference by booleanPreference(default = true)
    /** %1000 是振幅， /1000 是震动时间 eg:500100 震动500毫秒 震动强度是100 振幅在1-255之间*/
    var vibrateParams :Int = 100050 //
    val vibrateParamsPreference by intPreference(default = 100050)


    /*-----------------------------开发者页面部分----------------------------------*/
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

    /*--------------------------其他部分---------------------------------------*/

    /**提示如何编辑卡组的详细信息*/
    var hadShowTipHowToEditDeckMsg :Boolean = true
    val hadShowTipHowToEditDeckMsgPreference by booleanPreference(default = true)

    var hadShowTipHowToUseMoveSelect :Boolean = true
    val hadShowTipHowToUseMoveSelectPreference by booleanPreference(default = true)

}