package com.lyd.absolverdatabase.bridge.data.bean

data class VideoSearchData<T>(
    val page :Int,
    val pagesize :Int,
    val numResult :Int,
    val numPages :Int,
    val rqt_type :String,
    val result :List<out T>
)

data class BilibiliVideo(
    val type :String,
    val id :Int,
    val author :String,
    val mid :Long,
    val typename :String,
    val arcurl :String,
    val aid :Int,
    val bvid :String,
    val title :String,
    val description :String,
    val pic :String,
    val play :Int,
    val video_review :Int,
    val favorites :Int,
    val tag :String,
    val review :Int,
    val pubdate :Long,
    val senddate :Long,
    val duration :String,
    val like :Int,
    val upic :String,
    val danmaku :Int
)



