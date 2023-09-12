package com.lyd.absolverdatabase.ui.widgets

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.FilterItem
import com.lyd.absolverdatabase.bridge.data.bean.FilterOption
import com.lyd.absolverdatabase.bridge.data.bean.MoveEffect
import com.lyd.absolverdatabase.bridge.data.repository.SettingRepository
import com.lyd.absolverdatabase.utils.GsonUtils
import com.lyd.absolverdatabase.utils.logUtils.LLog
import java.math.RoundingMode
import java.text.DecimalFormat

class MovesFilterDialog(activity: Activity) :BaseBottomSheetDialog(activity) {

    private var mFilterOption :FilterOption ?= null

    private var filterCopy :FilterOption ?= null
    private var hasChanged = false


    private val twoDecimalFormat :DecimalFormat by lazy {
        DecimalFormat("#.00").apply {// 始终保留两位小数
            roundingMode = RoundingMode.FLOOR
        }
    }
    private val oneDecimalFormat :DecimalFormat by lazy {
        DecimalFormat("0.0").apply {
            roundingMode = RoundingMode.FLOOR
        }
    }
    private val rangeLabelFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        LabelFormatter { value: Float -> twoDecimalFormat.format(value) }
    }
    private val rangeLabelForOneDecimal by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        LabelFormatter { value: Float -> oneDecimalFormat.format(value) }
    }


    private lateinit var rootLinear :LinearLayout

    private lateinit var strengthLight :MaterialCheckBox
    private lateinit var strengthMid :MaterialCheckBox
    private lateinit var strengthHeavy :MaterialCheckBox

    private lateinit var rangeRSlider: RangeSlider
    private lateinit var rangeMin :TextView
    private lateinit var rangeMax :TextView

    private lateinit var effectChipGroup :ChipGroup
    private lateinit var effectStop :Chip
    private lateinit var effectDodgeUp :Chip
    private lateinit var effectDodgeLow :Chip
    private lateinit var effectDodgeSide :Chip
    private lateinit var effectBreakDefences :Chip
    private lateinit var effectSuperArmor :Chip
    private lateinit var effectBlockCounter :Chip
    private lateinit var effectDoubleAttack :Chip
    private lateinit var effectTripleAttack :Chip
    private lateinit var effectMidLine :Chip
    private lateinit var effectMentalBlow :Chip
    private lateinit var effectNull :Chip
    private lateinit var effectSelectAll :Chip
    private lateinit var effectClearAll :Chip

    private lateinit var startFrameRSlider :RangeSlider
    private lateinit var startFrameMin :TextView
    private lateinit var startFrameMax :TextView

    private lateinit var phyWeaknessRSlider :RangeSlider
    private lateinit var phyWeaknessMin :TextView
    private lateinit var phyWeaknessMax :TextView

    private lateinit var phyOutputRSlider :RangeSlider
    private lateinit var phyOutputMin :TextView
    private lateinit var phyOutputMax :TextView

    private lateinit var hitAdvRSlider :RangeSlider
    private lateinit var hitAdvMin :TextView
    private lateinit var hitAdvMax :TextView

    private lateinit var defAdvRSlider :RangeSlider
    private lateinit var defAdvMin :TextView
    private lateinit var defAdvMax :TextView



    override fun createView(inflater: LayoutInflater): View {
        val view :View = inflater.inflate(R.layout.dialog_moves_filter,null,false)

        hasChanged = false

        view.apply {
            rootLinear = findViewById(R.id.dialog_movesFilter_rootLinear)

            val tempFilterList :List<FilterItem> = GsonUtils.fromJson(SettingRepository.movesFilterListJson,GsonUtils.getListType(FilterItem::class.java))
            tempFilterList.forEach {
                if (it.isChecked){
                    when(it.tag){
                        FilterItem.STRENGTH -> initStrengthItem(this)
                        FilterItem.RANGE -> initRangeItem(this)
                        FilterItem.EFFECT -> initEffectItem(this)
                        FilterItem.START_FRAME -> initStartFrameItem(this)
                        FilterItem.PHYSICAL_WEAKNESS -> initPhyWeaknessItem(this)
                        FilterItem.PHYSICAL_OUTPUT -> initPhyOutItem(this)
                        FilterItem.HIT_ADVANTAGE_FRAME -> initHitAdvItem(this)
                        FilterItem.DEF_ADVANTAGE_FRAME -> initDefAdvItem(this)
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
            mutableListOf(filterOption.strengthList[0],filterOption.strengthList[1],filterOption.strengthList[2]),
            rangeRange = filterOption.rangeRange,
            effectSet = hashSetOf<String>().apply { addAll(filterOption.effectSet) },
            startFrameRange = filterOption.startFrameRange,
            phyWeaknessRange = filterOption.phyWeaknessRange,
            phyOutputRange = filterOption.phyOutputRange,
            hitAdvRange = filterOption.hitAdvRange,
            defAdvRange = filterOption.defAdvRange
        )
    }

    private fun initStrengthItem(view: View){
        val tempStrengthLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_strength,null,false)
        rootLinear.addView(tempStrengthLayout)
        view.apply {
            strengthLight = findViewById(R.id.dialog_movesFilter_strength_checkBox_light)
            strengthMid = findViewById(R.id.dialog_movesFilter_strength_checkBox_mid)
            strengthHeavy = findViewById(R.id.dialog_movesFilter_strength_checkBox_heavy)
        }

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
    private fun initRangeItem(view: View){
        val tempRangeLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_range,null,false)
        rootLinear.addView(tempRangeLayout)
        view.apply {
            rangeRSlider = findViewById(R.id.dialog_movesFilter_range_rangeSlider)
            rangeMin = findViewById(R.id.dialog_movesFilter_range_textMin)
            rangeMax = findViewById(R.id.dialog_movesFilter_range_textMax)
        }
        rangeRSlider.apply {
            setLabelFormatter(rangeLabelFormat)
            addOnChangeListener { slider, _, fromUser ->
                rangeMin.text = twoDecimalFormat.format(slider.values[0])
                rangeMax.text = twoDecimalFormat.format(slider.values[1])
                if (!fromUser) return@addOnChangeListener
                mFilterOption?.rangeRange = FilterOption.list2RangeForRange(slider.values)
            }
        }

        mFilterOption?.apply {
            rangeRSlider.values = FilterOption.range2ListForRange(rangeRange)
        }
    }
    private fun initEffectItem(view: View){
        val tempEffectLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_effect,null,false)
        rootLinear.addView(tempEffectLayout)
        view.apply {
            effectChipGroup = findViewById(R.id.dialog_movesFilter_effect_chipGroup)
            effectStop = findViewById(R.id.dialog_movesFilter_effect_chip_stop)
            effectDodgeUp = findViewById(R.id.dialog_movesFilter_effect_chip_dodge_up)
            effectDodgeLow = findViewById(R.id.dialog_movesFilter_effect_chip_dodge_low)
            effectDodgeSide = findViewById(R.id.dialog_movesFilter_effect_chip_dodge_side)
            effectBreakDefences = findViewById(R.id.dialog_movesFilter_effect_chip_break_defences)
            effectSuperArmor = findViewById(R.id.dialog_movesFilter_effect_chip_super_armor)
            effectBlockCounter = findViewById(R.id.dialog_movesFilter_effect_chip_block_counter)
            effectDoubleAttack = findViewById(R.id.dialog_movesFilter_effect_chip_double_attack)
            effectTripleAttack = findViewById(R.id.dialog_movesFilter_effect_chip_triple_attack)
            effectMidLine = findViewById(R.id.dialog_movesFilter_effect_chip_mid_line)
            effectMentalBlow = findViewById(R.id.dialog_movesFilter_effect_chip_mental_blow)
            effectNull = findViewById(R.id.dialog_movesFilter_effect_chip_null)
            effectSelectAll = findViewById(R.id.dialog_movesFilter_effect_chip_selectAll)
            effectClearAll = findViewById(R.id.dialog_movesFilter_effect_chip_clearAll)
        }

        effectSelectAll.setOnClickListener {
            effectChipGroup.forEach {
                if (it is Chip){
                    it.isChecked = true
                }
            }
            mFilterOption?.effectSet = hashSetOf(MoveEffect.STOP.name,MoveEffect.DODGE_UP.name,MoveEffect.DODGE_LOW.name,MoveEffect.DODGE_SIDE.name,MoveEffect.BREAK_DEFENCES.name,MoveEffect.SUPER_ARMOR.name,
                MoveEffect.BLOCK_COUNTER.name,MoveEffect.DOUBLE_ATTACK.name,MoveEffect.TRIPLE_ATTACK.name,MoveEffect.MID_LINE.name,MoveEffect.MENTAL_BLOW.name,MoveEffect.NULL.name)
        }
        effectClearAll.setOnClickListener {
            effectChipGroup.clearCheck()
            mFilterOption?.effectSet?.clear()
        }

        effectStop.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.STOP.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.STOP.name)
            }
        }
        effectDodgeUp.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.DODGE_UP.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.DODGE_UP.name)
            }
        }
        effectDodgeLow.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.DODGE_LOW.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.DODGE_LOW.name)
            }
        }
        effectDodgeSide.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.DODGE_SIDE.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.DODGE_SIDE.name)
            }
        }
        effectBreakDefences.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.BREAK_DEFENCES.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.BREAK_DEFENCES.name)
            }
        }
        effectSuperArmor.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.SUPER_ARMOR.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.SUPER_ARMOR.name)
            }
        }
        effectBlockCounter.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.BLOCK_COUNTER.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.BLOCK_COUNTER.name)
            }
        }
        effectDoubleAttack.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.DOUBLE_ATTACK.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.DOUBLE_ATTACK.name)
            }
        }
        effectTripleAttack.visibility = if (SettingRepository.useWhatDataMod == SettingRepository.CEMOD) View.VISIBLE else View.GONE
        effectTripleAttack.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.TRIPLE_ATTACK.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.TRIPLE_ATTACK.name)
            }
        }
        effectMidLine.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.MID_LINE.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.MID_LINE.name)
            }
        }
        effectMentalBlow.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.MENTAL_BLOW.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.MENTAL_BLOW.name)
            }
        }
        effectNull.setOnCheckedChangeListener { btn, isChecked ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            if (isChecked){
                mFilterOption?.effectSet?.add(MoveEffect.NULL.name)
            } else {
                mFilterOption?.effectSet?.remove(MoveEffect.NULL.name)
            }
        }

        mFilterOption?.apply {
            effectStop.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.STOP.name) == true
            effectDodgeUp.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.DODGE_UP.name) == true
            effectDodgeLow.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.DODGE_LOW.name) == true
            effectDodgeSide.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.DODGE_SIDE.name) == true
            effectBreakDefences.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.BREAK_DEFENCES.name) == true
            effectSuperArmor.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.SUPER_ARMOR.name) == true
            effectBlockCounter.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.BLOCK_COUNTER.name) == true
            effectDoubleAttack.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.DOUBLE_ATTACK.name) == true
            effectTripleAttack.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.TRIPLE_ATTACK.name) == true
            effectMidLine.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.MID_LINE.name) == true
            effectMentalBlow.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.MENTAL_BLOW.name) == true
            effectNull.isChecked = mFilterOption?.effectSet?.contains(MoveEffect.NULL.name) == true
        }
    }
    private fun initStartFrameItem(view: View){
        val tempStartFrameLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_start_frame,null,false)
        rootLinear.addView(tempStartFrameLayout)
        view.apply {
            startFrameRSlider = findViewById(R.id.dialog_movesFilter_startFrame_rangeSlider)
            startFrameMin = findViewById(R.id.dialog_movesFilter_startFrame_textMin)
            startFrameMax = findViewById(R.id.dialog_movesFilter_startFrame_textMax)
        }

        startFrameRSlider.addOnChangeListener { slider, _, fromUser ->
            startFrameMin.text = slider.values[0].toInt().toString()
            startFrameMax.text = slider.values[1].toInt().toString()
            if (!fromUser) return@addOnChangeListener
            mFilterOption?.startFrameRange = FilterOption.list2RangeForStartF(slider.values)
        }

        mFilterOption?.apply {
            startFrameRSlider.values = FilterOption.range2ListForStartF(startFrameRange)
        }
    }
    private fun initPhyWeaknessItem(view: View){
        val tempPhyWeaknessLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_phy_weakness,null,false)
        rootLinear.addView(tempPhyWeaknessLayout)
        view.apply {
            phyWeaknessRSlider = findViewById(R.id.dialog_movesFilter_phyWeakness_rangeSlider)
            phyWeaknessMin = findViewById(R.id.dialog_movesFilter_phyWeakness_textMin)
            phyWeaknessMax = findViewById(R.id.dialog_movesFilter_phyWeakness_textMax)
        }

        phyWeaknessRSlider.apply {
            setLabelFormatter(rangeLabelForOneDecimal)
            addOnChangeListener { slider, _, fromUser ->
                phyWeaknessMin.text = oneDecimalFormat.format(slider.values[0])
                phyWeaknessMax.text = oneDecimalFormat.format(slider.values[1])
                if (!fromUser) return@addOnChangeListener
                mFilterOption?.phyWeaknessRange = FilterOption.list2RangeForWeakness(slider.values)
            }
        }

        mFilterOption?.apply {
            phyWeaknessRSlider.values = FilterOption.range2ListForWeakness(phyWeaknessRange)
        }
    }

    private fun initPhyOutItem(view: View){
        val tempPhyOutputLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_phy_output,null,false)
        rootLinear.addView(tempPhyOutputLayout)
        view.apply {
            phyOutputRSlider = findViewById(R.id.dialog_movesFilter_phyOutput_rangeSlider)
            phyOutputMin = findViewById(R.id.dialog_movesFilter_phyOutput_textMin)
            phyOutputMax = findViewById(R.id.dialog_movesFilter_phyOutput_textMax)
        }

        phyOutputRSlider.apply {
            setLabelFormatter(rangeLabelForOneDecimal)
            addOnChangeListener { slider, _, fromUser ->
                phyOutputMin.text = oneDecimalFormat.format(slider.values[0])
                phyOutputMax.text = oneDecimalFormat.format(slider.values[1])
                if (!fromUser) return@addOnChangeListener
                mFilterOption?.phyOutputRange = FilterOption.list2RangeForOutput(slider.values)
            }
        }

        mFilterOption?.apply {
            phyOutputRSlider.values = FilterOption.range2ListForOutput(phyOutputRange)
        }
    }

    private fun initHitAdvItem(view: View){
        val tempHitAdvLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_hit_advantage,null,false)
        rootLinear.addView(tempHitAdvLayout)
        view.apply {
            hitAdvRSlider = findViewById(R.id.dialog_movesFilter_hitAdvantage_rangeSlider)
            hitAdvMin = findViewById(R.id.dialog_movesFilter_hitAdvantage_textMin)
            hitAdvMax = findViewById(R.id.dialog_movesFilter_hitAdvantage_textMax)
        }

        hitAdvRSlider.addOnChangeListener { slider, _, fromUser ->
            hitAdvMin.text = slider.values[0].toInt().toString()
            hitAdvMax.text = slider.values[1].toInt().toString()
            if (!fromUser) return@addOnChangeListener
            mFilterOption?.hitAdvRange = FilterOption.list2RangeForHitAdv(slider.values)
        }

        mFilterOption?.apply {
            hitAdvRSlider.values = FilterOption.range2ListForHitAdv(hitAdvRange)
        }
    }

    private fun initDefAdvItem(view: View){
        val tempDefAdvLayout :View = LayoutInflater.from(context).inflate(R.layout.filter_def_advantage,null,false)
        rootLinear.addView(tempDefAdvLayout)
        view.apply {
            defAdvRSlider = findViewById(R.id.dialog_movesFilter_defAdvantage_rangeSlider)
            defAdvMin = findViewById(R.id.dialog_movesFilter_defAdvantage_textMin)
            defAdvMax = findViewById(R.id.dialog_movesFilter_defAdvantage_textMax)
        }

        defAdvRSlider.addOnChangeListener { slider, _, fromUser ->
            defAdvMin.text = slider.values[0].toInt().toString()
            defAdvMax.text = slider.values[1].toInt().toString()
            if (!fromUser) return@addOnChangeListener
            mFilterOption?.defAdvRange = FilterOption.list2RangeForDefAdv(slider.values)
        }

        mFilterOption?.apply {
            defAdvRSlider.values = FilterOption.range2ListForDefAdv(defAdvRange)
        }
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

        filterCopy!!.strengthList.forEachIndexed { index, b ->
            if (mFilterOption!!.strengthList[index] != b){
                tempFlag ++
            }
        }

        if (mFilterOption!!.rangeRange != filterCopy!!.rangeRange)
            tempFlag++

        if (filterCopy?.effectSet?.size != mFilterOption?.effectSet?.size){
            tempFlag++
        } else if (filterCopy!!.effectSet != mFilterOption!!.effectSet){
            tempFlag++
        }

        if (mFilterOption!!.startFrameRange != filterCopy!!.startFrameRange){
            tempFlag++
        }

        if (mFilterOption!!.phyWeaknessRange != filterCopy!!.phyWeaknessRange)
            tempFlag++

        if (mFilterOption!!.phyOutputRange != filterCopy!!.phyOutputRange)
            tempFlag++

        if (mFilterOption!!.hitAdvRange != filterCopy!!.hitAdvRange)
            tempFlag++

        if (mFilterOption!!.defAdvRange != filterCopy!!.defAdvRange)
            tempFlag++

        return tempFlag != 0
    }
}