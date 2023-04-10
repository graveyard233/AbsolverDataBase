package com.lyd.absolverdatabase.bridge.data.bean


// 专门给搜索老G收藏的类
data class BilibiliMsg<T> (
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T
)

data class ArchiveData (
    val aids: List<Int>,
    val archives: List<Archive>,
    val meta: Meta,
    val page: Page
)

data class Archive (
    val aid: Long,
    val bvid: String,
    val ctime: Long,
    val duration: Int,
    val interactiveVideo: Boolean,
    val pic: String,
    val pubdate: Long,
    val stat: Stat,
    val state: Int,
    val title: String,
    val ugcPay: Int
)

data class Stat (
    val view: Int
)

data class Meta (
    val category: Long,
    val cover: String,
    val description: String,
    val mid: Int,
    val name: String,
    val ptime: Long,
    val seasonID: Int,
    val total: Int
)

data class Page (
    val pageNum: Int,
    val pageSize: Int,
    val total: Int
)

