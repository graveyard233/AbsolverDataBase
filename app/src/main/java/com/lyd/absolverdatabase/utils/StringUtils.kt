package com.lyd.absolverdatabase.utils

object StringUtils {

    private val azList :List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val sb = StringBuilder()

    fun getRandomString(size :Int = 10) :String
    = sb.apply {
        clear()
        (0..size).onEach { append(azList.random()) }
    }.toString()
}