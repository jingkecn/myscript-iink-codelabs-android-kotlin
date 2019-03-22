/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.extensions

import com.myscript.iink.Editor

fun Editor.convert() =
    getSupportedTargetConversionStates(null).firstOrNull()?.let { convert(null, it) }
