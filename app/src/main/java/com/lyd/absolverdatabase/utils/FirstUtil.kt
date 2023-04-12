package com.lyd.absolverdatabase.utils

object FirstUtil {
    private var isLearnFragmentFirst = 0

    fun isLearnFragmentFirst() :Boolean{
        return if (isLearnFragmentFirst == 0){
            isLearnFragmentFirst ++
            true
        } else {
            false
        }
    }
}