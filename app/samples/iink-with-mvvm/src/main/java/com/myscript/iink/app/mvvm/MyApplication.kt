/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm

import android.arch.persistence.room.Room
import com.myscript.iink.ContentPackage
import com.myscript.iink.PackageOpenOption
import com.myscript.iink.app.common.InteractiveInkApplication
import com.myscript.iink.app.mvvm.Constants.IINK_PACKAGE_NAME
import com.myscript.iink.app.mvvm.model.room.ContentDatabase
import java.io.File

@Suppress("unused")
class MyApplication : InteractiveInkApplication() {

    lateinit var contentPackage: ContentPackage private set

    override fun onCreate() {
        super.onCreate()
        // initialize content database.
        database = Room
            .databaseBuilder(this, ContentDatabase::class.java, "content_database")
            .build()
        val myPackageFile = File(filesDir, "$IINK_PACKAGE_NAME.iink")
        try {
            // open package or create a new one if it doesn't exist.
            contentPackage = engine.openPackage(myPackageFile, PackageOpenOption.CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTerminate() {
        contentPackage.close()
        super.onTerminate()
    }

    companion object {
        lateinit var database: ContentDatabase
        private val TAG = MyApplication::class.java.simpleName
    }
}
