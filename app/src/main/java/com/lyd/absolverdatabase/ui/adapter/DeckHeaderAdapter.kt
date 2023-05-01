package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseSingleItemAdapter
import com.lyd.absolverdatabase.R

class DeckHeaderAdapter :BaseSingleItemAdapter<Any,DeckHeaderAdapter.VH>() {

    class VH(view: View): RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: VH, item: Any?) {

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_deck_head,parent,false))
    }


}