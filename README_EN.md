<p align="right">
    <a href="https://www.github.com/graveyard233/AbsolverDatabase">中文</a>
    <span> | </span>
    <strong>English</strong>
</p>

<h1 align="center">
    <img src="https://i.postimg.cc/6QbkhKJy/AB-1.png" width="200">
    <br>AbsolverDatabase<br>
</h1>

<div align="center">
    <h3>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#description">
    Description
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#download">
    Download
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#use">
    Use
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#screenshot">
    Screenshot
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#thanks">
    Thanks
    </a>
    <span> | </span>
    <a href="https://github.com/graveyard233/AbsolverDatabase/blob/master/README_EN.md#licence">
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

The bottom menu navigation bar is divided into teach module, data module, card module and setting module. 
## Teach Module
- Browse Ghost beginner teaching videos by default, click on the top picture to pop up a pop-up window, you can switch the list.Another list displays the latest videos related to Absolver on Bilibili, with a default load of 20

## Data Module
- Default display map interface(fragment), can be zoomed in or out to view
- Color palette interface displays Material dynamic theme colors in devices according to your wallpaper
- Tip interface displays some data of the game

## Card Module

### Decks Interface
- On the right, filter the decks in the database by category (bare handed, boxed, sword), and on the left, list these decks in descending order of update time
- Click the deck to enter the card deck edit interface
- Long press the deck to copy the deck code(just like hearthstone deck code)
- Click on the top icon to create a new deck
- Long press the top icon to import the deck based on the code in the clipboard
- Side slide deck item, click (X) to delete deck

### Deck Edit Interface
- All operations on this interface will not be written to the database unless actively saved
- Long press the move box to delete the move inside the box (temporary)
- Click the save icon to save this edit (cannot be rolled back)
- Click the move icon to enter the move selection interface
- Long press the blank area to evoke the bottom pop-up window for editing card group names, etc

### Move Select Interface
- At the top is the move sequence, long press and hold the move within the sequence to delete it
- Click on the move to enter the editing status of the selected box
- The middle section contains move data and basic filtering items, and swiping the data column can open the advanced filtering pop-up window
- The bottom is lists of moves, distinguished by the end of the stand side, if it is used in other place, there is an icon in the upper left corner of the picture (App does not handle the case of repeated selection)

## Setting Module
- Can be used to learn about project repositories, application versions and ways to submit suggestions

### Config Interface
- Set various configurations for the application, with Gaussian blur and dynamic theme colors only available on Android 12 and above
- Advanced filtering can be enabled or disabled, long press to drag in order, sorted from left to right and from top to bottom
- It is not recommended to turn on more data display switches in CEMod, as it will calculate and adjust the UI after opening, which can cause obvious lag phenomenon

### Database interface
- Except for cloud synchronous CEMod method, all method will use local code to update moves data
- Cloud synchronous CE data is obtained through webView analysis of web pages, which carries the risk of failure

### Development interface
- Interface for adjusting log printing,write level and control crash log

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

Special thanks to Ghost for the prototype design and screening logic explanation of the card deck editor.

# [Licence](https://github.com/graveyard233/AbsolverDatabase/blob/master/LICENSE)

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