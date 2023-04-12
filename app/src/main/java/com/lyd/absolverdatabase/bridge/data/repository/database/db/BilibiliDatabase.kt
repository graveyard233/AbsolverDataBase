package com.lyd.absolverdatabase.bridge.data.repository.database.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lyd.absolverdatabase.bridge.data.bean.BilibiliVideo
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.BilibiliVideoDAO

@Database(entities = [BilibiliVideo::class], version = 1, exportSchema = false)
abstract class BilibiliDatabase :RoomDatabase(){

    abstract fun videoDao() : BilibiliVideoDAO

    companion object{

        @Volatile
        private var INSTANCE : BilibiliDatabase ?= null

        fun getDatabase(context: Context) : BilibiliDatabase {
            return INSTANCE ?: synchronized(this){
                val instant = Room.databaseBuilder(
                    context.applicationContext,
                    BilibiliDatabase::class.java,
                    "bilibili_database"
                ).addCallback(object :Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 在这里进行初始化工作
                        Log.i("BilibiliDatabase", "database onCreate: ")
                    }
                }).build()
                INSTANCE = instant
                instant
            }
        }
    }

}