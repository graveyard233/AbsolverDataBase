package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.absolverdatabase.utils.MoveGenerate
import com.lyd.absolverdatabase.utils.SideUtil

class MovesBar : ConstraintLayout {

    private val TAG = "${javaClass.simpleName}-${hashCode()}"

    private val originList = mutableListOf<MoveOrigin>(MoveGenerate.generateEmptyOriginMove(),
        MoveGenerate.generateEmptyOriginMove(),
        MoveGenerate.generateEmptyOriginMove())


    private lateinit var side0 :ImageView
    private lateinit var side1 :ImageView
    private lateinit var side2 :ImageView
    private lateinit var side3 :ImageView

    private lateinit var move0 :ImageView
    private lateinit var move1 :ImageView
    private lateinit var move2 :ImageView

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

        move0 = findViewById(R.id.bar_move_0)
        move1 = findViewById(R.id.bar_move_1)
        move2 = findViewById(R.id.bar_move_2)
    }

    // TODO: 1.判断listSize可靠性 2.比较glide和setImageBitmap的加载速度 3.按照招式来设置前后的方向 4.根据招式之间的方向来进行判断前后是否冲突
    // 这个算是初始化设置，不应该考虑冲突问题，冲突问题交给其他设置函数处理
    // 应该有个规定，假如是emptyMove
    fun initOriginMoves(moveList: List<MoveOrigin>){
        if (moveList.size != 3)
            return
//        move0.setImageBitmap(AssetsUtil.getBitmapByMoveId(context = context,idList[0]))
//        move1.setImageBitmap(AssetsUtil.getBitmapByMoveId(context = context,idList[1]))
//        move2.setImageBitmap(AssetsUtil.getBitmapByMoveId(context = context,idList[2]))

        // 先完成赋值
        Log.i(TAG, "initOriginMoves:size: ${originList.size} $originList")
        for (i in 0 until originList.size){
            originList[i] = moveList[i]
        }

        move0.setMoveImg(originList[0])
        move1.setMoveImg(originList[1])
        move2.setMoveImg(originList[2])

//        GlideApp.with(move0)
//            .load("${AssetsUtil.rootPath+AssetsUtil.movesPath}${idList[0]}.jpg")
//            .into(move0)


    }



    // TODO: 未完成，待完善
    private fun setSideImg(startView: ImageView?,endView: ImageView?,move: MoveOrigin){
        startView?.apply {
            GlideApp.with(this)
                .load(SideUtil.imgIdBySide(move.startSide))
                .into(this)
        }
        endView?.apply {
            GlideApp.with(this)
                .load(SideUtil.imgIdBySide(move.endSide))
                .into(this)
        }
    }

    private fun ImageView.setMoveImg(move :MoveOrigin){
        if (move.id >= 0){// 正常招式
            setImageBitmap(AssetsUtil.getBitmapByMoveId(context, moveId = move.id))
//            scaleType = ImageView.ScaleType.FIT_XY
            setBackgroundColor(resources.getColor(R.color.transparent))// 避免黑边和背景颜色对不上，所以要去掉背景色
        } else{// 空招式
            setImageResource(R.drawable.ic_add_move)
//            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
        }
    }
}