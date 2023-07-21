<p align="right">
    <a href="https://www.github.com/graveyard233/AbsolverDatabase">中文</a>
    <span> | </span>
    <strong>English</strong>
</p>

<h1 align="center">
    <br>AbsolverDatabase<br>
</h1>

<div align="center">
    <h3>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Description">
    Description
    </a>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Download">
    Download
    </a>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Use">
    Use
    </a>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Screenshot">
    Screenshot
    </a>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Thanks">
    Thanks
    </a>
    <span> | </span>
    <a href="https://www.github.com/graveyard233/AbsolverDatabase/README_EN.md#Licence">
    Licence
    </a>
    </h3>
</div>

# Description

A modern Android app for Absolver players, which provides functions such as adding, deleting, correcting and checking decks, browsing and filtering moves, importing and sharing deck, and viewing game maps;
Provide Mode switching and data support for China Edition Mode for Chinese players, and can synchronize CEMod moves data through webView;
Mainly uses Kotlin language, adopts Jetpack component, uses MVVM architecture (view-View-model-repository), can run in Android 10 and above version, UI reference Ghost's deck editor;
Use Navigation (relying on the database to ensure that network requests will not be repeated after replacing fragment by navigation), use Room for the database, use DataStore and MMKV for configuration data, and use flow combined with viewModel to realize data transmission in different fragments. Network requests use Retrofit + Okhttp + coroutines;
Designed according to the Material Design 3 specification, provide background image dynamic color acquisition and multiple theme color switching on Android12 and above; Suitable for night mode, suitable for horizontal screen mode; Support English view;

# Download

<a>coming soon</a>

# Use

Click the text on the right side of the deck interface to switch the type, and all card groups are sorted by update time

Create a new deck: Click on the plus image in the deck browsing interface

Import deck: Long press and hold the plus image in the deck browsing interface. By default, there is a pop-up prompt that will read the data from the clipboard

Share deck: Long press and hold the deck item in the deck browsing interface to write deck data to the clipboard

Delete deck: Left swipe the deck item and click the Delete icon

To add a new move to the attack sequence: Click the plus icon and click the move after filtering. The move will be temporarily stored in the sequence

Removes a move from the attack sequence: Long press the move image in the sequence

Edit deck's name, etc. : Long press the blank area in the deck editing interface, the bottom pop-up window will pop up

Save the deck: Click the save icon in the card group editing interface; Note that only after saving is written to the database, the deck set will not be updated if not saved

Browse the latest video from Bilibili: Teaching interface Click the big picture above, the selection pop-up window will pop up

Configuration items: On the Settings screen, click the Config item

Move Data synchronization: Click Database related on the Settings screen

# Screenshot

----
|[![AB-deck-Fragment.jpg](https://i.postimg.cc/9QdZ9dVy/AB-deck-Fragment.jpg)](https://postimg.cc/3kwk5Dkw)|[![AB-deck-Edit-dark1.jpg](https://i.postimg.cc/KYsVMknr/AB-deck-Edit-dark1.jpg)](https://postimg.cc/yJcvqNpk)|[![AB-move-CE1.jpg](https://i.postimg.cc/kgctj7HH/AB-move-CE1.jpg)](https://postimg.cc/Wdd3zcTm)|[![AB-deck-Edit-light-Land.jpg](https://i.postimg.cc/Rhhmw5Y2/AB-deck-Edit-light-Land.jpg)](https://postimg.cc/7GFjycwN)|
| --- | --- | --- | --- |
----

# Thanks

AbsolverDatabase has received assistance from many open source projects and technology blogs.
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


# Licence

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