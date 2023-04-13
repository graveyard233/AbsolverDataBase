package com.lyd.absolverdatabase.utils

object FirstUtil {
    private var isLearnVideoFirst = 0

    fun isLearnVideoSearchFirst() :Boolean{
        return if (isLearnVideoFirst == 0){
            isLearnVideoFirst ++
            true
        } else false
    }

    private var isVideosFirst = 0

    fun isVideosSearchFirst() :Boolean{
        return if (isVideosFirst == 0){
            isVideosFirst ++
            true
        } else false
    }
}