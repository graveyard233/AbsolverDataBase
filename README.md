<p align="right">
    <strong>中文</strong>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md">English</a>
</p>

<h1 align="center">
    <img src="https://i.postimg.cc/6QbkhKJy/AB-1.png" width="200">
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

[**Release**](https://github.com/graveyard233/AbsolverDatabase/releases)

# 使用

底部菜单导航栏分为教学模块，数据模块，卡组模块和设置模块
<details>
    <summary><strong>教学模块</strong></summary>
<p>默认浏览Ghost新手教学视频，点击顶部图片弹出弹窗，可切换列表。另一列表展示B站最新的赦免者相关视频，默认加载20个</p>
</details>

<details>
    <summary><strong>数据模块</strong></summary>
<p>默认展示地图界面，可放大缩小查看</p>
<p>调色板界面根据手机壁纸展示Material动态主题色</p>
<p>Tip界面展示游戏的一些数据</p>
</details>

<details>
    <summary><strong>卡组模块</strong></summary>
<h3>卡组浏览界面</h3>
<p>右侧按类别(徒手，拳套，剑)筛选数据库中的卡组，左侧列表展示此类卡组(按卡组更新时间降序排列)</p>
<p>点击卡组可进入卡组编辑界面</p>
<p>长按卡组可复制卡组代码</p>
<p>点击顶部图标可新建卡组</p>
<p>长按顶部图标可根据剪贴板内的代码复制卡组</p>
<p>侧滑卡组子项，点击(X)可以删除卡组</p>
<h3>卡组编辑界面</h3>
<p>本界面一切操作，不主动保存的话，不会写入数据库</p>
<p>长按招式框可删除框内招式(临时)</p>
<p>点击保存图标可以保存本次编辑(无法回滚)</p>
<p>点击招式图标可进入招式选择界面</p>
<p>长按空白区域可唤起底部弹窗，用于编辑卡组名等</p>
<h3>招式选择界面</h3>
<p>顶部为招式序列，长按序列内招式可将其删除</p>
<p>点击招式即进入选中框的编辑状态</p>
<p>中间部分为招式数据和初级筛选项，侧滑数据那栏可以打开高级筛选弹窗</p>
<p>底部为招式列表，按结束站架区分，若已使用则在图片左上角有图标提示(应用不处理重复选中的情况)</p>
</details>

<details>
    <summary><strong>设置模块</strong></summary>
<p>可用于了解项目仓库，应用版本和提交建议的方式</p>
<h3>基础设置界面</h3>
<p>设置应用各种基础配置，动态主题色仅在Android12及以上可用</p>
<p>如果觉得顶部的工具栏很难看，可以关闭[显示工具栏]，其仅提供导航功能</p>
<p>点击高级设置可进入高级设置界面</p>
<p>不推荐打开CEMod的更多数据展示开关，因为打开后会计算并调整UI，会造成明显的卡顿现象</p>
<h3>高级设置界面</h3>
<p>高斯模糊功能仅在Android12及以上可用</p>
<p>高级筛选可开启或停用高级筛选项，长按可拖动顺序，按从左到右，从上到下排序</p>
<h3>数据库相关界面</h3>
<p>除云端同步CEMod方法外，都是本地代码更新招式数据</p>
<p>云端同步CE数据是通过webView分析网页的方式获取数据，有失败的风险</p>
<h3>开发工具界面</h3>
<p>用于调整日志打印，写入等级，控制崩溃日志的界面</p>
</details>

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

特别感谢Ghost的卡组编辑器的原型设计与筛选逻辑讲解

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
