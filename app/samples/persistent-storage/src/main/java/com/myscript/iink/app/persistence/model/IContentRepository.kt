/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.persistence.model

import android.arch.lifecycle.LiveData

interface IContentRepository {
    val contents: LiveData<List<Content>>
    fun insert(vararg content: Content)
}
