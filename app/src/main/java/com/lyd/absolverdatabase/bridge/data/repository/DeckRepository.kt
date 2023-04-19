package com.lyd.absolverdatabase.bridge.data.repository

import android.util.Log
import com.lyd.absolverdatabase.bridge.data.bean.TestABCrossRef
import com.lyd.absolverdatabase.bridge.data.bean.TestDataGenerate
import com.lyd.absolverdatabase.bridge.data.repository.database.dao.TestDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeckRepository(private val testDao : TestDAO) {

    private val TAG = javaClass.simpleName
    init {
        CoroutineScope(Dispatchers.IO).launch {
            testDao.deleteAWithB()
            Log.i(TAG, "init: delete aWithB")
            testDao.deleteAllA()
            testDao.upsertAllA(TestDataGenerate.generateTestA())
            Log.i(TAG, "init: A")
            testDao.deleteAllB()
            testDao.upsertAllB(TestDataGenerate.generateTestB())
            Log.i(TAG, "init: B")

            try {
                val tempList = mutableListOf<TestABCrossRef>()
                tempList.add(TestABCrossRef(aid = 0, bid = 1, attackType = "序列攻击","左上"))

                testDao.upsetAAndB(tempList)

                Log.i(TAG, "init result before: ${testDao.getAWithBs()}")

                testDao.deleteOneB(bid = 1)

                Log.i(TAG, "init result after: ${testDao.getAWithBs()}")
//                这个查询会查出所有a+和a有关系的b的列表，但是
//                这个AWithB是不知道TestABCrossRef这个类的内部结构的，导致更深层次关系不好处理，
//                你要依靠这个listB来获取bid，然后去查TestABCrossRef这个表，拿到所有的关系实体类，然后才能筛选序列类型
//                这样算查询了三次，有点浪费性能，反正都是靠id来维持关系，直接存id会好一些
//                TODO: 所以我认为，卡组应该持有招式的id列表，然后靠字段名（列表名）来区分序列攻击等
            } catch (e: Exception) {
                Log.e(TAG, "init: error", e)
            }
        }
    }

}