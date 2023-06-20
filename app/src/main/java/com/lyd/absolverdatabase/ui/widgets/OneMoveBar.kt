package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.material.imageview.ShapeableImageView
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.absolverdatabase.utils.SideUtil

class OneMoveBar :LinearLayout{

    private lateinit var startSide :ImageView
    private lateinit var endSide :ImageView
    private lateinit var img :ShapeableImageView


    constructor(context: Context) :this(context,null)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet){
        LayoutInflater.from(context).inflate(R.layout.bar_one_move,this)

        val ats = context.obtainStyledAttributes(attributeSet, R.styleable.OneMoveBar)
        val initStartSide = ats.getInt(R.styleable.OneMoveBar_startSide,0)
        val initEndSide = ats.getInt(R.styleable.OneMoveBar_endSide,0)
        ats.recycle()

        findViews()

        startSide.setImageResource(SideUtil.imgIdForOneMove(initStartSide))
        endSide.setImageResource(SideUtil.imgIdForOneMove(initEndSide))
    }


    fun initClick(clickProxy :(view:View)->Unit = {},longClickProxy :(view:View)->Unit = {}){
        img.setOnClickListener {
            clickProxy.invoke(it)
        }
        img.setOnLongClickListener {
            longClickProxy.invoke(it)
            return@setOnLongClickListener true
        }
    }

    private fun findViews(){
        startSide = findViewById(R.id.bar_oneMove_side_start)
        endSide = findViewById(R.id.bar_oneMove_side_end)
        img = findViewById(R.id.bar_oneMove_img)
    }

    fun updateMove(box :MoveBox){
        if (box.moveId != -1){
            GlideApp.with(img)
                .load(AssetsUtil.getBitmapByMoveId(context,box.moveId))
                .into(img)
            img.setBackgroundColor(resources.getColor(R.color.transparent))
            box.moveOrigin?.endSide?.let { SideUtil.imgIdForOneMove(SideUtil.getIntBySide(it)) }
                ?.let { endSide.setImageResource(it) }
        } else {
            img.setImageResource(R.drawable.ic_add_move)
            img.setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
            endSide.setImageDrawable(startSide.drawable)
        }
    }

}