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
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.Style
import com.lyd.absolverdatabase.utils.StyleUtil
import com.lyd.absolverdatabase.utils.TimeUtils.getDateYear
import com.lyd.absolverdatabase.utils.TimeUtils.toDateStr

class DeckAdapter :BaseQuickAdapter<Deck,DeckAdapter.VH>()  {

    private val TAG = javaClass.simpleName

    override fun onBindViewHolder(holder: VH, position: Int, item: Deck?) {
        item?.apply {
            GlideApp.with(holder.imgStyle)
                .load(StyleUtil.styleId(deckStyle))
                .error(R.drawable.ic_video_load_error)
                .into(holder.imgStyle)
            holder.deckName.text = name
            holder.note.text = note
            var dateFormat = "yyyy/MM/dd"
            if (updateTime.getDateYear() == System.currentTimeMillis().getDateYear())
                dateFormat = context.resources.getString(R.string.dateFormat_for_deckAdapter)
            holder.aboutTime.text = context.resources.getString(R.string.item_deck_aboutTime,
                updateTime.toDateStr(dateFormat)
            )
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_deck,parent,false))
    }

    inner class VH(item :View):RecyclerView.ViewHolder(item) {
        val imgStyle = item.findViewById<ImageView>(R.id.item_deck_img_style)
        val imgDelete = item.findViewById<ImageView>(R.id.item_deck_img_delete)// 点击删除事件应该写在外部
        val deckName = item.findViewById<TextView>(R.id.item_deck_name)
        val note = item.findViewById<TextView>(R.id.item_deck_note)
        val aboutTime = item.findViewById<TextView>(R.id.item_deck_aboutTime)
    }

}