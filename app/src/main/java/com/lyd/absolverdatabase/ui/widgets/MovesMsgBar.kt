package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin

class MovesMsgBar :ConstraintLayout{

    private lateinit var text0Left :TextView
    private lateinit var text0Right :TextView
    private lateinit var text1Left :TextView
    private lateinit var text1Right :TextView
    private lateinit var text2Left :TextView
    private lateinit var text2Right :TextView

    constructor(context: Context) :this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context,attrs){
        LayoutInflater.from(context).inflate(R.layout.bar_moves_msg,this)

        text0Left = findViewById(R.id.bar_moves_msg_text0_left)
        text0Right = findViewById(R.id.bar_moves_msg_text0_right)
        text1Left = findViewById(R.id.bar_moves_msg_text1_left)
        text1Right = findViewById(R.id.bar_moves_msg_text1_right)
        text2Left = findViewById(R.id.bar_moves_msg_text2_left)
        text2Right = findViewById(R.id.bar_moves_msg_text2_right)

    }


    fun updateMsg(boxes: List<MoveBox>){
        boxes.forEachIndexed { index, box ->
            when(index){
                0->{
                    updateOneMsg(text0Left,text0Right,box)
                }
                1->{
                    updateOneMsg(text1Left,text1Right,box)
                }
                2->{
                    updateOneMsg(text2Left,text2Right,box)
                }
            }
        }
    }

    private fun updateOneMsg(left :TextView,right :TextView,box: MoveBox){
        if (box.moveId == -1){
            left.clearText()
            right.clearText()
            return
        }
        box.moveOrigin?.apply {
            left.text = "+$hitAdvantageFrame/${if (defenseAdvantageFrame > 0) "+" else ""}$defenseAdvantageFrame"
            right.text = "${startFrame}帧"
        }
        box.moveCE?.apply {
            left.text = "+$hitAdvantageFrame/${if (defenseAdvantageFrame > 0) "+" else ""}$defenseAdvantageFrame"
            right.text = "${startFrame}帧"
        }
    }

    private fun TextView.clearText(){
        if (this.text.isNotEmpty()){
            this.text = ""
        }
    }
}