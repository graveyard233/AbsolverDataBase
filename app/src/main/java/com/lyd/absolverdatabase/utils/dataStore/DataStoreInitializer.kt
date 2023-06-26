package com.lyd.absolverdatabase.utils.dataStore

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class DataStoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        IDataStoreOwner.application = context as Application
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}