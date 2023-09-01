package com.lyd.absolverdatabase.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.lyd.absolverdatabase.utils.logUtils.LLog

object AssetsUtil {

    private val TAG = javaClass.simpleName

    val rootPath :String = "file:///android_asset/"

    val movesPath:String = "moves/"

    val jpgSuffix = ".jpg"
    val pngSuffix = ".png"


    fun getBitmapByMoveId(context: Context, moveId :Int):Bitmap?{
        var tempBitmap :Bitmap ?= null
        try {
            context.resources.assets.open("$movesPath$moveId${if (moveId >= 198) pngSuffix else jpgSuffix}").use {
                tempBitmap = BitmapFactory.decodeStream(it)
            }
        }catch (e :Exception){
            LLog.e(TAG, "获取bitmap出错",e)
        }
        return tempBitmap
    }

}