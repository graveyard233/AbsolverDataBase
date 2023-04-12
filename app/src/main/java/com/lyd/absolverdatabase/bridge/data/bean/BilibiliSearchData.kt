package com.lyd.absolverdatabase.bridge.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

data class VideoSearchData<T>(
    val page :Int,
    val pagesize :Int,
    val numResult :Int,
    val numPages :Int,
    val rqt_type :String,
    val result :List<out T>
)
@Entity(
    tableName = "bilibili_video"
)
data class BilibiliVideo(
    val type :String,
    val id :Int,
    val author :String,
    val mid :Long,
    val typename :String,
    val arcurl :String,
    val aid :Int,
    @PrimaryKey
    val bvid :String,
    var title :String,// 需要是var，因为title有可能包含keyword标签
    val description :String,
    val pic :String,
    val play :Int,
    val video_review :Int,
    val favorites :Int,
    val tag :String,
    val review :Int,
    val pubdate :Long,
    val senddate :Long,// 这个是秒级的时间戳
    val duration :String,
    val like :Int,
    val upic :String,
    val danmaku :Int
)



