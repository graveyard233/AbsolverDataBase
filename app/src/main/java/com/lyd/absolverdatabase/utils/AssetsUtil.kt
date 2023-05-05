package com.lyd.absolverdatabase.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

object AssetsUtil {

    private val TAG = javaClass.simpleName

    val rootPath :String = "file:///android_asset/"

    val movesPath:String = "moves/"


    fun getBitmapByMoveId(context: Context, moveId :Int):Bitmap?{
        var tempBitmap :Bitmap ?= null
        try {
            context.resources.assets.open("$movesPath$moveId.jpg").use {
                tempBitmap = BitmapFactory.decodeStream(it)
            }
        }catch (e :Exception){
            Log.e(TAG, "getBitmapByMoveId: ", e)
        }
        return tempBitmap
    }

}