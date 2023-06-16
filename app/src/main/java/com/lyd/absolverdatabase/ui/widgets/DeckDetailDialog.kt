package com.lyd.absolverdatabase.ui.widgets

import android.app.Activity
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputLayout
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.data.bean.Style
import com.lyd.absolverdatabase.utils.StyleUtil
import com.lyd.absolverdatabase.utils.TimeUtils.toDateStr
import java.util.Date

class DeckDetailDialog(activity: Activity) : BaseBottomSheetDialog(activity) {
    private val TAG = javaClass.simpleName

    var mDeck : Deck? = null

    private lateinit var _deckName :TextInputLayout
    private lateinit var _deckNote :TextInputLayout
    private lateinit var _imgDeckStyle :ImageView
    private lateinit var _createTime :TextView
    private lateinit var _updateTime :TextView
    private lateinit var _buttonGroup :MaterialButtonToggleGroup

    private val _format = "yyyy-MM-dd HH:mm:ss"

    override fun createView(inflater: LayoutInflater): View {
        val view :View = inflater.inflate(R.layout.dialog_deck_detail,null,false)

        view.apply {
            _deckName = findViewById(R.id.dialog_deckDetail_title)
            _deckNote = findViewById(R.id.dialog_deckDetail_note)
            _imgDeckStyle = findViewById(R.id.dialog_deckDetail_style)
            _createTime = findViewById(R.id.dialog_deckDetail_createTime)
            _updateTime = findViewById(R.id.dialog_deckDetail_updateTime)
            _buttonGroup = findViewById<MaterialButtonToggleGroup>(R.id.dialog_deckDetail_buttonGroup).apply {
                addOnButtonCheckedListener(object :MaterialButtonToggleGroup.OnButtonCheckedListener{
                    override fun onButtonChecked(
                        group: MaterialButtonToggleGroup?,
                        checkedId: Int,
                        isChecked: Boolean
                    ) {
                        if (!isChecked)// 注意，这里的取消选择和触发选择都会触发这个监听，所以要靠isCheck来判断这个是不是选择事件
                            return
                        when(checkedId){
                            R.id.dialog_deckDetail_select_Kahlt ->{
                                mDeck?.deckStyle = Style.KAHLT
                                _imgDeckStyle.setImageResource(R.drawable.ic_kahlt)
                            }
                            R.id.dialog_deckDetail_select_Forsaken ->{
                                mDeck?.deckStyle = Style.FORSAKEN
                                _imgDeckStyle.setImageResource(R.drawable.ic_forsaken)
                            }
                            R.id.dialog_deckDetail_select_Winfall ->{
                                mDeck?.deckStyle = Style.WINDFALL
                                _imgDeckStyle.setImageResource(R.drawable.ic_windfall)
                            }
                            R.id.dialog_deckDetail_select_Stagger ->{
                                mDeck?.deckStyle = Style.STAGGER
                                _imgDeckStyle.setImageResource(R.drawable.ic_stagger)
                            }
                            R.id.dialog_deckDetail_select_Faejin ->{
                                mDeck?.deckStyle = Style.FAEJIN
                                _imgDeckStyle.setImageResource(R.drawable.ic_faejin)
                            }
                        }
                    }

                })
            }
        }

        mDeck?.apply {
            if (name.isNotBlank())
                _deckName.editText?.setText(name)
            if (note.isNotBlank())
                _deckNote.editText?.setText(note)
            _imgDeckStyle.setImageResource(StyleUtil.styleId(deckStyle))
            _buttonGroup.check(getIdByStyle(deckStyle))
            _createTime.text = context.resources.getString(R.string.deckDetail_createTime,if (createTime == 0L) {
                Date().time.toDateStr(_format)
            } else {
                createTime.toDateStr(_format)
            })
            _updateTime.text =context.resources.getString(R.string.deckDetail_updateTime,if (updateTime == 0L) {
                Date().time.toDateStr(_format)
            } else {
                updateTime.toDateStr(_format)
            })
        }?.apply {
            _deckName.editText?.addTextChangedListener(
                afterTextChanged = {text: Editable? ->
                    text?.let {
                        if (it.isNotBlank())// 不能为空，所以要判断
                            mDeck?.name = it.toString()
                    }
                }
            )
            _deckNote.editText?.addTextChangedListener(afterTextChanged = {text: Editable? ->
                text?.let {
                    mDeck?.note = it.toString()// 可空，所以不需要判断isNotBlank
                }
            })
        }

        return view
    }

    @IdRes
    private fun getIdByStyle(style: Style):Int{
        return when(style){
            Style.KAHLT -> R.id.dialog_deckDetail_select_Kahlt
            Style.FORSAKEN ->R.id.dialog_deckDetail_select_Forsaken
            Style.WINDFALL ->R.id.dialog_deckDetail_select_Winfall
            Style.STAGGER ->R.id.dialog_deckDetail_select_Stagger
            Style.FAEJIN ->R.id.dialog_deckDetail_select_Faejin
            else ->R.id.dialog_deckDetail_select_Winfall
        }
    }

}