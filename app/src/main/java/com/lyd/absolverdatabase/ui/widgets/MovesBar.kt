package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lyd.absolverdatabase.R

class MovesBar : ConstraintLayout {

    private lateinit var side0 :ImageView
    private lateinit var side1 :ImageView
    private lateinit var side2 :ImageView
    private lateinit var side3 :ImageView

    constructor(context: Context) :this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context,attrs) {
        LayoutInflater.from(context).inflate(R.layout.bar_moves,this)

        val ats = context.obtainStyledAttributes(attrs,R.styleable.MovesBar)
        val initSide = ats.getResourceId(R.styleable.MovesBar_startStandSide,R.drawable.ic_upper_right)
        ats.recycle()

        findViews()


        side0.setImageResource(initSide)
        side1.setImageResource(initSide)
        side2.setImageResource(initSide)
        side3.setImageResource(initSide)
    }

    private fun findViews(){
        side0 = findViewById(R.id.bar_move_side_0)
        side1 = findViewById(R.id.bar_move_side_1)
        side2 = findViewById(R.id.bar_move_side_2)
        side3 = findViewById(R.id.bar_move_side_3)
    }
}