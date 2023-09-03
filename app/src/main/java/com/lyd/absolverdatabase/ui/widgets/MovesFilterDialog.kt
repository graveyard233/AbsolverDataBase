package com.lyd.absolverdatabase.ui.widgets

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.checkbox.MaterialCheckBox
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.FilterOption
import com.lyd.absolverdatabase.utils.logUtils.LLog

class MovesFilterDialog(activity: Activity) :BaseBottomSheetDialog(activity) {

    private var mFilterOption :FilterOption ?= null

    private var filterCopy :FilterOption ?= null
    private var hasChanged = false

    private lateinit var strengthLight :MaterialCheckBox
    private lateinit var strengthMid :MaterialCheckBox
    private lateinit var strengthHeavy :MaterialCheckBox


    override fun createView(inflater: LayoutInflater): View {
        val view :View = inflater.inflate(R.layout.dialog_moves_filter,null,false)

        hasChanged = false

        view.apply {
            strengthLight = findViewById(R.id.dialog_movesFilter_strength_checkBox_light)
            strengthMid = findViewById(R.id.dialog_movesFilter_strength_checkBox_mid)
            strengthHeavy = findViewById(R.id.dialog_movesFilter_strength_checkBox_heavy)

            strengthLight.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                mFilterOption?.strengthList?.set(0, isChecked)
                doAfterChange()
            }
            strengthMid.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                mFilterOption?.strengthList?.set(1, isChecked)
                doAfterChange()
            }
            strengthHeavy.setOnCheckedChangeListener { btn, isChecked ->
                if (!btn.isPressed) return@setOnCheckedChangeListener
                mFilterOption?.strengthList?.set(2, isChecked)
                doAfterChange()
            }

            mFilterOption?.apply {
                strengthList.forEachIndexed { index, boolean ->
                    when(index){
                        0 -> strengthLight.isChecked = boolean
                        1 -> strengthMid.isChecked = boolean
                        2 -> strengthHeavy.isChecked = boolean
                    }
                }
            }
        }

        return view
    }

    fun initFilter(filterOption: FilterOption){
        mFilterOption = filterOption
        filterCopy = FilterOption(
            filterOption.attackToward,
            filterOption.attackAltitude,
            filterOption.attackDirection,
            mutableListOf(filterOption.strengthList[0],filterOption.strengthList[1],filterOption.strengthList[2])
        )
    }

    private fun doAfterChange(){
        hasChanged = true
    }

    fun hasReallyChanged() :Boolean{
        if (mFilterOption == null)
            return false
        if (filterCopy == null)
            return false
        var tempFlag = 0
        LLog.d(TAG, "copy:${filterCopy?.strengthList}")
        LLog.d(TAG, "mList:${mFilterOption?.strengthList}")
        filterCopy!!.strengthList.forEachIndexed { index, b ->
            if (mFilterOption!!.strengthList[index] != b){
                tempFlag ++
            }
        }


        return tempFlag != 0
    }
}