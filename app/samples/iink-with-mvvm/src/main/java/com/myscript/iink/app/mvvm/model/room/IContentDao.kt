/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.model.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.myscript.iink.app.mvvm.model.Content

@Dao
interface IContentDao {
    val contents: LiveData<List<Content>>
        @Query("SELECT * FROM content_table ORDER BY contentPackage ASC") get

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg content: Content)
}
