package com.lyd.absolverdatabase.bridge.data.repository.database.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lyd.absolverdatabase.bridge.data.bean.*
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.BilibiliVideoDAO
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.TestDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Database(entities = [BilibiliVideo::class,TestA::class,TestB::class,TestABCrossRef::class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){

    abstract fun videoDao() : BilibiliVideoDAO

    abstract fun testDao() : TestDAO

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
                                try {
                                    testDao().deleteAWithB()
                                    Log.i("AppDatabase", "onCreate: delete aWithB")
                                    testDao().deleteAllA()
                                    testDao().upsertAllA(TestDataGenerate.generateTestA())
                                    Log.i("AppDatabase", "onCreate: A")
                                    testDao().deleteAllB()
                                    testDao().upsertAllB(TestDataGenerate.generateTestB())
                                    Log.i("AppDatabase", "onCreate: B")

                                    testDao().upsetAAndB(listOf(TestABCrossRef(aid = 0, bid = 1, attackType = "序列攻击","左上")))

                                    delay(500)
                                    val temp = testDao().getAWithBs()
                                    Log.i("AppDatabase", "onCreate result: $temp")
                                } catch (e: Exception) {
                                    Log.e("AppDatabase", "onDBCreate: ", e)
                                }
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