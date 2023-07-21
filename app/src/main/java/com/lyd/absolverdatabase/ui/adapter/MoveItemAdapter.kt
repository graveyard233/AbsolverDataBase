package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.AssetsUtil
import java.util.*

class MoveItemAdapter :BaseQuickAdapter<MoveForSelect,MoveItemAdapter.VH>() {

    override fun onBindViewHolder(holder: VH, position: Int, item: MoveForSelect?) {
        item?.run {
            GlideApp.with(holder.img)
                .load(AssetsUtil.getBitmapByMoveId(context, moveId = if (SettingRepository.isUseCNEditionMod) item.moveCE.id else item.moveOrigin.id))
                .error(R.drawable.ic_video_load_error)
                .into(holder.img)
            val tempId = if (SettingRepository.isUseCNEditionMod) item.moveCE.id else item.moveOrigin.id
            holder.img.setBackgroundColor(context.resources.getColor(if (tempId >= 198) R.color.img_add_move_bg else R.color.transparent))
            holder.moveName.text =
                if (SettingRepository.isUseCNEditionMod) {
                    if (Locale.getDefault().toLanguageTag().startsWith("zh")){
                        moveCE.name
                    } else {
                        moveCE.name_en.ifEmpty { moveCE.name }
                    }
                } else {
                    if (Locale.getDefault().toLanguageTag().startsWith("zh")){
                        moveOrigin.name
                    } else {
                        moveOrigin.name_en
                    }
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