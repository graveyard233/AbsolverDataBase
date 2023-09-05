package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.AttackAltitude
import com.lyd.absolverdatabase.bridge.data.bean.AttackDirection
import com.lyd.absolverdatabase.bridge.data.bean.AttackToward
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository

class OneMoveMsgBar :ConstraintLayout {

    private lateinit var textLeft :TextView
    private lateinit var textRight :TextView

    constructor(context: Context) :this(context,null)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet){
        LayoutInflater.from(context).inflate(R.layout.bar_one_move_msg,this)

        textLeft = findViewById(R.id.bar_oneMove_msg_textLeft)
        textRight = findViewById(R.id.bar_oneMove_msg_textRight)
    }

    fun updateOneMsg(oneBox: MoveBox){
        if (!SettingRepository.isShowMovesMsgInDeckEdit){
            return
        }
        if (oneBox.moveId == -1){
            textLeft.clearText()
            textRight.clearText()
            return
        }
        oneBox.moveOrigin?.apply {
            if (SettingRepository.showWhatMsgInDeckEdit == 0){
                textLeft.text = context.getString(R.string.move_msg_bar_advantageFrame,hitAdvantageFrame,
                    "${if (defenseAdvantageFrame > 0) "+" else ""}$defenseAdvantageFrame")
                textRight.text = context.getString(R.string.move_msg_bar_startFrame,startFrame)
            } else {
                "${
                    when (attackToward) {
                        AttackToward.LEFT -> context.getString(R.string.attackToward_left)
                        AttackToward.RIGHT -> context.getString(R.string.attackToward_right)
                    }
                }${
                    when (attackAltitude) {
                        AttackAltitude.LOW -> context.getString(R.string.attackAltitude_low)
                        AttackAltitude.MIDDLE -> context.getString(R.string.attackAltitude_middle)
                        AttackAltitude.HEIGHT -> context.getString(R.string.attackAltitude_height)
                    }
                }${context.getString(R.string.altitude)}${
                    when (attackDirection) {
                        AttackDirection.HORIZONTAL -> context.getString(R.string.attackDirection_horizontal)
                        AttackDirection.VERTICAL -> context.getString(R.string.attackDirection_vertical)
                        AttackDirection.THRUST -> context.getString(R.string.attackDirection_thrust)
                    }
                }".let {
                    textLeft.text = it
                }
                textRight.text = ""
            }
        }
        oneBox.moveCE?.apply {
            if (SettingRepository.showWhatMsgInDeckEdit == 0){
                textLeft.text = context.getString(R.string.move_msg_bar_advantageFrame,hitAdvantageFrame,
                    "${if (defenseAdvantageFrame > 0) "+" else ""}$defenseAdvantageFrame")
                textRight.text = context.getString(R.string.move_msg_bar_startFrame,startFrame)
            } else {
                "${
                    when (attackToward) {
                        AttackToward.LEFT -> context.getString(R.string.attackToward_left)
                        AttackToward.RIGHT -> context.getString(R.string.attackToward_right)
                    }
                }${
                    when (attackAltitude) {
                        AttackAltitude.LOW -> context.getString(R.string.attackAltitude_low)
                        AttackAltitude.MIDDLE -> context.getString(R.string.attackAltitude_middle)
                        AttackAltitude.HEIGHT -> context.getString(R.string.attackAltitude_height)
                    }
                }${context.getString(R.string.altitude)}${
                    when (attackDirection) {
                        AttackDirection.HORIZONTAL -> context.getString(R.string.attackDirection_horizontal)
                        AttackDirection.VERTICAL -> context.getString(R.string.attackDirection_vertical)
                        AttackDirection.THRUST -> context.getString(R.string.attackDirection_thrust)
                    }
                }".let {
                    textLeft.text = it
                }
                textRight.text = ""
            }
        }
    }

    private fun TextView.clearText(){
        if (this.text.isNotEmpty()){
            this.text = ""
        }
    }
}