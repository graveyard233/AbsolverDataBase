package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.AssetsUtil
import com.lyd.architecture.utils.DisplayUtils
import java.util.*

class MoveItemAdapter :BaseQuickAdapter<MoveForSelect,MoveItemAdapter.VH>() {

    companion object {
        private val cutAppearanceModel by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            ShapeAppearanceModel.builder().apply {
                setTopLeftCorner(CornerFamily.CUT, DisplayUtils.dp2px(10f).toFloat())
                setBottomRightCorner(CornerFamily.CUT, DisplayUtils.dp2px(10f).toFloat())
//                    setLeftEdge(TriangleEdgeTreatment(10f,true))
//                    setRightEdge(TriangleEdgeTreatment(10f,true))
                setTopEdge(TriangleEdgeTreatment(DisplayUtils.dp2px(4f).toFloat(), true))
                setBottomEdge(TriangleEdgeTreatment(DisplayUtils.dp2px(4f).toFloat(), true))
            }.build()
        }

        private val normalAppearanceModel by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            ShapeAppearanceModel.builder()/*.apply {
                    setTopLeftCorner(CornerFamily.CUT,0f)
                }*/.build()
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: MoveForSelect?) {
        item?.run {
            holder.img.scaleType = if (SettingRepository.moveItemsInOneRow <= 2){
                ImageView.ScaleType.FIT_CENTER
            } else {
                ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(holder.img)
                .load(AssetsUtil.getBitmapByMoveId(context, move.id))
                .error(R.drawable.ic_video_load_error)
                .into(holder.img)
            holder.img.setBackgroundColor(context.resources.getColor(if (move.id >= 198) R.color.img_add_move_bg else R.color.transparent))
            holder.moveName.text =
                if (Locale.getDefault().toLanguageTag().startsWith("zh")){
                    move.name
                } else {
                    move.name_en.ifEmpty { move.name }
                }
            if (SettingRepository.whichUsedMoveTag == 0){
                if (item.isSelected){
                    val drawableRes = MaterialShapeDrawable(cutAppearanceModel).apply {
                        paintStyle = Paint.Style.STROKE
                        strokeWidth = DisplayUtils.dp2px(3.5f).toFloat()
                        strokeColor = holder.img.strokeColor
                    }
                    holder.img.shapeAppearanceModel = cutAppearanceModel
                    holder.img.foreground = drawableRes
                } else {
                    holder.img.foreground = null
                    holder.img.shapeAppearanceModel = normalAppearanceModel
                }
            } else {
                holder.imgSelect.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_move,parent,false))
    }


    inner class VH(item :View) :RecyclerView.ViewHolder(item){
        val img = item.findViewById<ShapeableImageView>(R.id.item_move_img)
        val moveName = item.findViewById<TextView>(R.id.item_move_name)
        val imgSelect = item.findViewById<ImageView>(R.id.item_move_isSelect)
    }

}