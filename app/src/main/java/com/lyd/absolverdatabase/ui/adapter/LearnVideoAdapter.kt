package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Archive
import com.lyd.absolverdatabase.utils.TimeUtils.toDateStr

class LearnVideoAdapter : BaseQuickAdapter<Archive, LearnVideoAdapter.VH>() {


    override fun onBindViewHolder(holder: VH, position: Int, item: Archive?) {
        item?.run {
            Glide.with(holder.img)
                .load(pic)
                .placeholder(R.drawable.ic_video_loading)
                .error(R.drawable.ic_video_load_error)
                .into(holder.img)

            holder.title.text = title
            holder.author.text = context.resources.getString(R.string.item_learn_author,"Ghost-O")

            holder.videoMsg.text = context.resources.getString(R.string.item_learn_videoMsg,stat.view,(pubdate * 1000).toDateStr("yyyy/MM/dd"))
            Log.i("TAG", "onBindViewHolder: ${holder.videoMsg.text}")
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_learn_video,parent,false))
    }


    class VH(item : View) : RecyclerView.ViewHolder(item) {
        val img = item.findViewById<ImageView>(R.id.item_learn_img)
        val title = item.findViewById<TextView>(R.id.item_learn_title)
        val author = item.findViewById<TextView>(R.id.item_learn_author)
        val videoMsg = item.findViewById<TextView>(R.id.item_learn_videoMsg)
    }

}