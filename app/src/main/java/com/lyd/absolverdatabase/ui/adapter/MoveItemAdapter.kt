package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.MoveForSelect
import com.lyd.absolverdatabase.bridge.data.bean.MoveOrigin
import com.lyd.absolverdatabase.utils.AssetsUtil

class MoveItemAdapter :BaseQuickAdapter<MoveForSelect,MoveItemAdapter.VH>() {

    override fun onBindViewHolder(holder: VH, position: Int, item: MoveForSelect?) {
        item?.run {
            GlideApp.with(holder.img)
                .load(AssetsUtil.getBitmapByMoveId(context, moveId = item.moveOrigin.id))
                .error(R.drawable.ic_video_load_error)
                .into(holder.img)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_move,parent,false))
    }


    inner class VH(item :View) :RecyclerView.ViewHolder(item){
        val img = item.findViewById<ImageView>(R.id.item_move_img)
    }

}