package com.lyd.absolverdatabase.bridge.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * @param whatBarToEdit 0~3 序列攻击 4~7 自选攻击
 * @param whatMoveBeClicked 标记序列中哪个招式被选中，但貌似没有任何作用，以后可以删掉
 * */
@Parcelize
class EditToSelectMsg(@androidx.annotation.IntRange(0,7) val whatBarToEdit:Int,val whatMoveBeClicked :Int = 0) : Parcelable
{
    companion object{
        const val SEQ_UPPER_RIGHT = 0
        const val SEQ_UPPER_LEFT = 1
        const val SEQ_LOWER_LEFT = 2
        const val SEQ_LOWER_RIGHT = 3

        const val OPT_UPPER_RIGHT = 4
        const val OPT_UPPER_LEFT = 5
        const val OPT_LOWER_LEFT = 6
        const val OPT_LOWER_RIGHT = 7
    }
}
