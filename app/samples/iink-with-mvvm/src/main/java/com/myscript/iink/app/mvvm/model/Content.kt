/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import com.myscript.iink.app.mvvm.Constants.IINK_PACKAGE_NAME

@Entity(tableName = "content_table")
data class Content(
    @NonNull @PrimaryKey
    val contentPackage: String = IINK_PACKAGE_NAME,
    val contentPart: String
)
