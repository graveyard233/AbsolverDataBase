package com.lyd.absolverdatabase.utils.crashUtils

import android.app.Application
import android.content.Context

class JavaCrashHandler private constructor():Thread.UncaughtExceptionHandler{



    private var mDefaultHandler :Thread.UncaughtExceptionHandler? = null

    private lateinit var crashCallback: ICrashCallback

    private lateinit var context :Context

    companion object{
        @JvmStatic
        private val mHandler = JavaCrashHandler()

        fun get() :JavaCrashHandler = mHandler
    }

    fun init(ctx :Application,iCrashCallback: ICrashCallback){
        context = ctx
        crashCallback = iCrashCallback

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()!!

        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(p0: Thread, p1: Throwable) {

//        if (mDefaultHandler != null){
//            Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler)
//        }

        crashCallback.onCrash(p1)

        mDefaultHandler?.uncaughtException(p0,p1)




    }


}