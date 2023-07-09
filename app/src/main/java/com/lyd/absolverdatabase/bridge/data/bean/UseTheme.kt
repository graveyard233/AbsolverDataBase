package com.lyd.absolverdatabase.bridge.data.bean

sealed class UseTheme(id :Int){
    companion object{
        const val DefaultId = 0
        const val WallpaperId = 1
        const val RedId = 2
        const val YellowId = 3
        const val BlueId = 4
        const val GreenId = 5
    }
    object Default :UseTheme(DefaultId)
    object Wallpaper :UseTheme(WallpaperId)
    object Red :UseTheme(RedId)
    object Yellow :UseTheme(YellowId)
    object Blue :UseTheme(BlueId)
    object Green :UseTheme(GreenId)
}
