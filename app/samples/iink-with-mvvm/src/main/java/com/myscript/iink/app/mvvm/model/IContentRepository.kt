/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.model

import android.arch.lifecycle.LiveData

interface IContentRepository {
    val contents: LiveData<List<Content>>
    fun insert(vararg content: Content)
}
