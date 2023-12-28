package com.lyd.absolverdatabase.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.dragswipe.listener.DragAndSwipeDataCallback
import com.google.android.material.checkbox.MaterialCheckBox
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.FilterItem

class MovesFilterAdapter :BaseQuickAdapter<FilterItem,MovesFilterAdapter.VH>(),DragAndSwipeDataCallback {



    override fun onBindViewHolder(holder: VH, position: Int, item: FilterItem?) {
        item?.apply {
            holder.checkBox.text = name
            holder.checkBox.isChecked = isChecked
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_moves_filter,parent,false))
    }

    override fun dataMove(fromPosition: Int, toPosition: Int) {
        move(fromPosition, toPosition)
    }

    override fun dataRemoveAt(position: Int) {
        removeAt(position)
    }

    class VH(item : View) : RecyclerView.ViewHolder(item) {
        val checkBox = item.findViewById<MaterialCheckBox>(R.id.item_movesFilter_checkbox)
    }
}