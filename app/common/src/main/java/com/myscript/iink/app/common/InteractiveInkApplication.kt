/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.common

import android.app.Application
import com.myscript.certificate.MyCertificate
import com.myscript.iink.Engine
import java.io.File

@Suppress("unused")
open class InteractiveInkApplication : Application(), IInteractiveInkApplication {

    final override lateinit var engine: Engine
        private set

    override fun onCreate() {
        super.onCreate()
        // Create MyScript interactive ink engine.
        // Please make sure that you have a valid active certificate.
        // If not, please get one from MyScript Developer:
        // - https://developer.myscript.com/getting-started
        engine = Engine.create(MyCertificate.getBytes()).apply {
            // configure MyScript interactive ink engine.
            configuration?.let {
                // configure the directories where to find *.conf.
                it.setStringArray(
                    "configuration-manager.search-path",
                    arrayOf("zip://$packageCodePath!/assets/conf")
                )
                // configure a temporary directory.
                it.setString("content-package.temp-folder", "${filesDir.path}${File.separator}tmp")
                // debug
                it.setBoolean("renderer.debug.draw-arc-outlines", DBG)
                it.setBoolean("renderer.debug.draw-object-boxes", DBG)
                it.setBoolean("renderer.debug.draw-text-boxes", DBG)
                if (DBG) it.setString("debug.log-file", File(filesDir, LOG_FILE_NAME).absolutePath)
            }
        }
    }

    override fun onTerminate() {
        // close MyScript iink runtime to release resources.
        engine.close()
        super.onTerminate()
    }

    companion object {
        private const val LOG_FILE_NAME = "my_iink_log_file"
        private val DBG = BuildConfig.DEBUG
    }
}
