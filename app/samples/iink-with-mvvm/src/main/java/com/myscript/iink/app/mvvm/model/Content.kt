/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.model

import com.myscript.iink.app.mvvm.Constants.IINK_PACKAGE_NAME

data class Content(
    val contentPackage: String = IINK_PACKAGE_NAME,
    val contentPart: String
)
