/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.annotation.NonNull
import com.myscript.iink.Editor
import com.myscript.iink.MimeType

fun Editor.convert() =
    getSupportedTargetConversionStates(null).firstOrNull()?.let { convert(null, it) }

fun Editor.copyToClipboard(@NonNull context: Context, type: MimeType): String =
    export_(null, type).also {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.primaryClip =
            ClipData.newPlainText(type.name, it)
    }
