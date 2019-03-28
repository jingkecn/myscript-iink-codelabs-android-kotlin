/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.myscript.iink.ContentPart
import com.myscript.iink.app.mvvm.BuildConfig
import com.myscript.iink.app.mvvm.model.Content
import kotlin.properties.Delegates

class ContentViewModel : ViewModel() {
    val content = MutableLiveData<Content>()

    var contentPart: ContentPart? by Delegates.observable<ContentPart?>(null) { property, oldValue, newValue ->
        if (newValue?.id == oldValue?.id) return@observable
        if (DBG) Log.d(TAG, "on $property changed: { ${oldValue?.id} → ${newValue?.id} }.")
        newValue?.let { content.postValue(Content(contentPart = it.id)) }
    }

    companion object {
        private val DBG = BuildConfig.DEBUG
        private val TAG = ContentViewModel::class.java.simpleName
    }
}
