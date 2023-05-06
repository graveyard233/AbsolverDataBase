package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.utils.SideUtil

class OneMoveBar :LinearLayout{

    private lateinit var startSide :ImageView
    private lateinit var endSide :ImageView
    private lateinit var img :ImageView


    constructor(context: Context) :this(context,null)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet){
        LayoutInflater.from(context).inflate(R.layout.bar_one_move,this)

        val ats = context.obtainStyledAttributes(attributeSet, R.styleable.OneMoveBar)
        val initStartSide = ats.getInt(R.styleable.OneMoveBar_startSide,0)
        val initEndSide = ats.getInt(R.styleable.OneMoveBar_endSide,0)
        ats.recycle()

        findViews()

        startSide.setImageResource(SideUtil.imgId(initStartSide))
        endSide.setImageResource(SideUtil.imgId(initEndSide))
    }


    fun initClick(clickProxy :(view:View)->Unit = {}){
        img.setOnClickListener {
            clickProxy.invoke(it)
        }
    }

    private fun findViews(){
        startSide = findViewById(R.id.bar_oneMove_side_start)
        endSide = findViewById(R.id.bar_oneMove_side_end)
        img = findViewById(R.id.bar_oneMove_img)
    }

}