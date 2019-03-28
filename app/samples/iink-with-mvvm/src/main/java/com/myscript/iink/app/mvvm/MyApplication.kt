/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm

import com.myscript.iink.ContentPackage
import com.myscript.iink.PackageOpenOption
import com.myscript.iink.app.common.InteractiveInkApplication
import com.myscript.iink.app.mvvm.Constants.IINK_PACKAGE_NAME
import java.io.File

@Suppress("unused")
class MyApplication : InteractiveInkApplication() {

    lateinit var contentPackage: ContentPackage private set

    override fun onCreate() {
        super.onCreate()
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
        private val TAG = MyApplication::class.java.simpleName
    }
}
