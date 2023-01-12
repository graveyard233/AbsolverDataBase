package com.lyd.absolverdatabase.bridge.data.bean

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BilibiliMsg<T> (
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T
)

@JsonClass(generateAdapter = true)
data class ArchiveData (
    val aids: List<Int>,
    val archives: List<Archive>,
    val meta: Meta,
    val page: Page
)

@JsonClass(generateAdapter = true)
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

@JsonClass(generateAdapter = true)
data class Stat (
    val view: Int
)

@JsonClass(generateAdapter = true)
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

@JsonClass(generateAdapter = true)
data class Page (
    val pageNum: Int,
    val pageSize: Int,
    val total: Int
)

