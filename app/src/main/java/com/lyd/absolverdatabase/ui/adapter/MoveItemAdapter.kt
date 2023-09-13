package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.AssetsUtil
import java.util.*

class MoveItemAdapter :BaseQuickAdapter<MoveForSelect,MoveItemAdapter.VH>() {

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
            holder.imgSelect.visibility = if (item.isSelected) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_move,parent,false))
    }


    inner class VH(item :View) :RecyclerView.ViewHolder(item){
        val img = item.findViewById<ImageView>(R.id.item_move_img)
        val moveName = item.findViewById<TextView>(R.id.item_move_name)
        val imgSelect = item.findViewById<ImageView>(R.id.item_move_isSelect)
    }

}