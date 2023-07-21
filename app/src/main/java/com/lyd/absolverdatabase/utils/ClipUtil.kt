package com.lyd.absolverdatabase.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.lyd.architecture.utils.Utils

object ClipUtil {
    fun copyText(text :String){
        val clipboardManager = Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboardManager.setPrimaryClip(ClipData.newPlainText("",text))

        // 这里不应该弹toast
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
//            Toast.makeText(Utils.getApp(),"Copy in clipboard",Toast.LENGTH_SHORT).show()
//        }
    }

    fun readText() :String{
        val clipboardManager = Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = clipboardManager.primaryClip

        return if (clipData != null && clipData.itemCount > 0){
            clipData.getItemAt(0).text.toString()
        } else {
            "null"
        }
    }
}