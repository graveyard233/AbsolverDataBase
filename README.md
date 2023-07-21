<p align="right">
    <strong>中文</strong>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md">English</a>
</p>

<h1 align="center">
    <br>AbsolverDatabase<br>
</h1>

<div align="center">
    <h3>
    <a href="https://github.com/graveyard233/AbsolverDatabase#描述">
    描述
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase#下载">
    下载
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase#使用">
    使用
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase#截图">
    截图
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase#感谢">
    感谢
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase#许可证">
    许可证
    </a>
    </h3>
</div>

# 描述

为赦免者玩家打造的现代化Android应用，提供对卡组进行增删改查，浏览与筛选招式，导入与分享，查看游戏地图等功能；
提供针对中国玩家的China Edition Mode的模式切换与数据支持，能够通过webView网络同步CEMod招式数据；
主要使用Kotlin语言，采用Jetpack组件，使用MVVM架构（视图-视图模型-仓库），可在Android 10及以上的版本运行，UI参考了Ghost的卡组编辑器；
导航使用Navigation（依靠数据库确保不会重复网络请求），数据库使用Room，配置数据采用DataStore与MMKV，采用flow结合viewModel来实现数据在不同fragment的传输，网络请求使用Retrofit + Okhttp + 协程；
依照Material Design 3规范设计，在Android12及以上提供背景图动态颜色获取和多种主题色切换；适配夜间模式，适配横屏模式；支持英文查看；

# 下载

<strong>coming soon</strong>

# 使用


通过点击卡组界面的右侧文本进行类型切换，所有卡组按更新时间排序

创建新卡组:卡组浏览界面中点击加号

导入卡组:卡组浏览界面中长按加号，默认情况下有提示弹窗，会读取剪贴板内的数据

分享卡组:卡组浏览界面中长按卡组，会将卡组数据写入剪贴板

删除卡组:卡组项左滑点击删除图标

在攻击序列中添加新的招式:点击加号，通过筛选后点击招式，这个招式会临时存储到这个序列中

在攻击序列中移除招式:序列中长按招式图片

编辑卡组名称等:在卡组编辑界面中长按空白区域，会弹出底部弹窗

保存卡组:在卡组编辑界面中点击保存图标；注意，只有保存后才会写入数据库，不保存将不会更新卡组

浏览B站最新视频:教学界面点击上面的大图，会弹出选择弹窗

配置项:在设置界面点击可配置项

招式数据同步:在设置界面点击数据库相关

# 截图

----
|[![AB-deck-Fragment.jpg](https://i.postimg.cc/9QdZ9dVy/AB-deck-Fragment.jpg)](https://postimg.cc/3kwk5Dkw)|[![AB-deck-Edit-dark1.jpg](https://i.postimg.cc/KYsVMknr/AB-deck-Edit-dark1.jpg)](https://postimg.cc/yJcvqNpk)|[![AB-move-CE1.jpg](https://i.postimg.cc/kgctj7HH/AB-move-CE1.jpg)](https://postimg.cc/Wdd3zcTm)|[![AB-deck-Edit-light-Land.jpg](https://i.postimg.cc/Rhhmw5Y2/AB-deck-Edit-light-Land.jpg)](https://postimg.cc/7GFjycwN)|
| --- | --- | --- | --- |
----

# 感谢

本项目受到许多开源项目与技术博客的帮助
- [MMKV](https://github.com/Tencent/MMKV)
- [Gson](https://github.com/google/gson)
- [Retrofit](https://github.com/square/retrofit)
- [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [Glide](https://github.com/bumptech/glide)
- [Subsampling-scale-image-view](https://github.com/davemorrissey/subsampling-scale-image-view)
- [SwipeMenuRecyclerView](https://github.com/aitsuki/SwipeMenuRecyclerView)
- [Jsoup](https://github.com/jhy/jsoup)
- [commons-text](https://central.sonatype.com/artifact/org.apache.commons/commons-text/1.10.0)
- [AgentWeb](https://github.com/Justson/AgentWeb)
- [Bilibili-API](https://github.com/SocialSisterYi/bilibili-API-collect)

# [许可证](https://github.com/graveyard233/AbsolverDatabase/blob/master/LICENSE)

        Copyright (C) 2023  graveyard233

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
