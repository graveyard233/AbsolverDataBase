package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyd.absolverdatabase.GlideApp
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.utils.TimeUtils.toDateStr

class SearchVideoAdapter : BaseQuickAdapter<BilibiliVideo, SearchVideoAdapter.VH>() {

    override fun onBindViewHolder(holder: VH, position: Int, item: BilibiliVideo?) {
        item?.run {
            GlideApp.with(holder.img)
                .load("https:$pic")
                .placeholder(R.drawable.ic_video_loading)
                .error(R.drawable.ic_video_load_error)
                .into(holder.img)

            holder.title.text = title
            holder.author.text = context.resources.getString(R.string.item_learn_author,author)/*String.format(context.resources.getString(R.string.item_learn_author),author)*/
//            holder.videoMsg.text = "播放量:$play · ${senddate.toDateStr("MM月dd日")}"

            holder.videoMsg.text = context.resources.getString(R.string.item_learn_videoMsg,play,(senddate * 1000).toDateStr(context.resources.getString(R.string.dateFormat_for_videoAdapter)))
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_learn_video,parent,false))
    }

    inner class VH(item :View) : RecyclerView.ViewHolder(item) {
        val img = item.findViewById<ImageView>(R.id.item_learn_img)
        val title = item.findViewById<TextView>(R.id.item_learn_title)
        val author = item.findViewById<TextView>(R.id.item_learn_author)
        val videoMsg = item.findViewById<TextView>(R.id.item_learn_videoMsg)
    }

}