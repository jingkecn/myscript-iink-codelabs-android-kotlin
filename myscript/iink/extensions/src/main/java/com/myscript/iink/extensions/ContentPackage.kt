/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.extensions

import com.myscript.iink.ContentPackage
import com.myscript.iink.ContentPart

val ContentPackage.parts: List<ContentPart>
    get() = arrayListOf<ContentPart>().also { for (i in 0 until partCount) it.add(getPart(i)) }
