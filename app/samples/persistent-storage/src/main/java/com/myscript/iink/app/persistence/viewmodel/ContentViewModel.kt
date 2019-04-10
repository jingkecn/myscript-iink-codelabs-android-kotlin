/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.persistence.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.myscript.iink.ContentPart
import com.myscript.iink.app.persistence.BuildConfig
import com.myscript.iink.app.persistence.model.Content
import com.myscript.iink.app.persistence.model.IContentRepository
import com.myscript.iink.app.persistence.model.room.RoomRepository
import kotlin.properties.Delegates

class ContentViewModel(
    private val repository: IContentRepository = RoomRepository()
) : ViewModel() {
    val contents = repository.contents

    var contentPart: ContentPart? by Delegates.observable<ContentPart?>(null) { property, oldValue, newValue ->
        if (newValue?.id == oldValue?.id) return@observable
        if (DBG) Log.d(TAG, "on $property changed: { ${oldValue?.id} → ${newValue?.id} }.")
        newValue?.let { repository.insert(Content(contentPart = it.id)) }
    }

    companion object {
        private val DBG = BuildConfig.DEBUG
        private val TAG = ContentViewModel::class.java.simpleName
    }
}
