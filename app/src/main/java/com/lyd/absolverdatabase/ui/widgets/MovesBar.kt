package com.lyd.absolverdatabase.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveBox
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.absolverdatabase.utils.SideUtil

class MovesBar : ConstraintLayout {

    private val TAG = "${javaClass.simpleName}-${hashCode()}"

    private val boxList = mutableListOf<MoveBox>(MoveBox(), MoveBox(), MoveBox())


    private lateinit var side0 :ImageView
    private lateinit var side1 :ImageView
    private lateinit var side2 :ImageView
    private lateinit var side3 :ImageView

    private lateinit var move0 :ShapeableImageView
    private lateinit var move1 :ShapeableImageView
    private lateinit var move2 :ShapeableImageView

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
    fun updateMoves(moveList: List<MoveBox>){
        if (moveList.size != 3)
            return
        // 先完成赋值
        boxList.forEachIndexed { index, moveBox ->
            boxList[index] = moveList[index]
        }
//        move0.setMoveImg(originList[0])
//        move1.setMoveImg(originList[1])
//        move2.setMoveImg(originList[2])

        updateOneMove(0)
        updateOneMove(1)
        updateOneMove(2)
//        GlideApp.with(move0)
//            .load("${AssetsUtil.rootPath+AssetsUtil.movesPath}${idList[0]}.jpg")
//            .into(move0)


    }

    fun initClick(clickProxy :(view:View,clickWhatMove :Int)->Unit = { _: View, _: Int -> },longClickProxy :(view:View,clickWhatMove :Int)->Unit = {_,_ ->}){
        move0.setOnClickListener { view ->
            clickProxy.invoke(view,0)
        }
        move0.setOnLongClickListener(){ view ->
            longClickProxy.invoke(view,0)
            return@setOnLongClickListener true
        }
        move1.setOnClickListener { view ->
            clickProxy.invoke(view,1)
        }
        move1.setOnLongClickListener { view ->
            longClickProxy.invoke(view,1)
            return@setOnLongClickListener true
        }
        move2.setOnClickListener { view ->
            clickProxy.invoke(view,2)
        }
        move2.setOnLongClickListener { view ->
            longClickProxy.invoke(view,2)
            return@setOnLongClickListener true
        }
    }

    private fun updateOneMove(position :Int){
        when(position){
            0 ->{
                changeMoveImg(move0,boxList[position].moveId)
                if (SettingRepository.isUseCNEditionMod){
                    boxList[position].moveCE?.apply {
                        GlideApp.with(side1)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side1)
                    }
                } else {
                    boxList[position].moveOrigin?.apply {
                        GlideApp.with(side1)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side1)
                    }
                }
            }
            1 ->{
                changeMoveImg(move1,boxList[position].moveId)
                if (SettingRepository.isUseCNEditionMod){
                    boxList[position].moveCE?.apply {
                        GlideApp.with(side1)
                            .load(SideUtil.imgIdForMoves(startSide))
                            .into(side1)
                        GlideApp.with(side2)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side2)
                    }
                } else {
                    boxList[position].moveOrigin?.apply {
                        GlideApp.with(side1)
                            .load(SideUtil.imgIdForMoves(startSide))
                            .into(side1)
                        GlideApp.with(side2)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side2)
                    }
                }
            }
            2 ->{
                changeMoveImg(move2,boxList[position].moveId)
                if (SettingRepository.isUseCNEditionMod){
                    boxList[position].moveCE?.apply {
                        GlideApp.with(side2)
                            .load(SideUtil.imgIdForMoves(startSide))
                            .into(side2)
                        GlideApp.with(side3)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side3)
                    }
                } else {
                    boxList[position].moveOrigin?.apply {
                        GlideApp.with(side2)
                            .load(SideUtil.imgIdForMoves(startSide))
                            .into(side2)
                        GlideApp.with(side3)
                            .load(SideUtil.imgIdForMoves(endSide))
                            .into(side3)
                    }
                }
            }
        }
    }

    private fun changeMoveImg(imageView: ImageView,moveId :Int){
        if (moveId > 0){
            GlideApp.with(imageView)
                .load(AssetsUtil.getBitmapByMoveId(context, moveId = moveId))
                .into(imageView)
            imageView.setBackgroundColor( resources.getColor(if (moveId >= 198) R.color.img_add_move_bg else R.color.transparent))// 避免黑边和背景颜色对不上，所以要去掉背景色
        } else {
            imageView.setImageResource(R.drawable.ic_add_move)
            imageView.setBackgroundColor(resources.getColor(R.color.img_add_move_bg))
        }
    }
}