package com.lyd.absolverdatabase.bridge.data.repository.database.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.JsonTxt
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.BilibiliVideoDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.DeckDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveGPDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveJsDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.MoveOriginDAO
import com.lyd.absolverdatabase.utils.GsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BilibiliVideo::class,Deck::class,MoveJson::class,MoveOrigin::class,MoveGP::class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){

    abstract fun videoDao() : BilibiliVideoDAO

    abstract fun deckDao() :DeckDAO

    abstract fun moveJsDao() :MoveJsDAO

    abstract fun moveOriginDao() :MoveOriginDAO

    abstract fun moveGPDao() :MoveGPDAO

    companion object{

        @Volatile
        private var INSTANCE : AppDatabase ?= null

        fun getDatabase(context: Context) : AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instant = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "absolver_database"
                ).addCallback(object :Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 在这里进行初始化工作
                        Log.i("AppDatabase", "database onCreate: start")

                        INSTANCE?.apply {
                            CoroutineScope(Dispatchers.IO).launch {

                                moveOriginDao().upsertAll(GsonUtils.fromJson(JsonTxt.moveOriginJson,GsonUtils.getListType(MoveOrigin::class.java)))

                                deckDao().upsertAll(listOf(
                                    Deck(
                                        name = "测试卡组_1",
                                        deckType = DeckType.HAND,
                                        deckStyle = Style.FORSAKEN,
                                        createTime = System.currentTimeMillis(),
                                        updateTime = System.currentTimeMillis(),
                                        note = "这是测试的卡组",
                                        sequenceUpperRight = mutableListOf(MoveBox(1,0),MoveBox(20,1),MoveBox()),
                                        sequenceUpperLeft = mutableListOf(MoveBox(13,1),MoveBox(),MoveBox()),
                                        sequenceLowerLeft = mutableListOf(MoveBox(),MoveBox(27,1),MoveBox()),
                                        sequenceLowerRight = mutableListOf(MoveBox(92,0),MoveBox(),MoveBox(35,1)),
                                        optionalUpperRight = MoveBox(42,1),
                                        optionalUpperLeft = MoveBox(),
                                        optionalLowerLeft = MoveBox(86,1),
                                        optionalLowerRight = MoveBox()
                                    )
                                ))
                            }
                        }
                    }
                }).build()
                INSTANCE = instant
                instant
            }
        }
    }

}