package com.lyd.absolverdatabase.bridge.data.bean

/**
 * @param isMirror 标记这个招式是不是镜像使用的，1表示已经为镜像，连着[moveOrigin]内的数据已经改变了
 * */
data class MoveForSelect(
    var move: Move,
    var isSelected :Boolean = false,
    var isMirror :Int = 0
)
