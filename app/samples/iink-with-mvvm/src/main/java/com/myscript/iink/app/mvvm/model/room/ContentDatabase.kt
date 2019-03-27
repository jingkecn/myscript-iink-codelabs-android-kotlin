/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.model.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.myscript.iink.app.mvvm.model.Content

@Database(entities = [(Content::class)], version = 1, exportSchema = false)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun dao(): IContentDao
}
