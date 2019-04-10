/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.persistence.model.room

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.myscript.iink.app.persistence.MyApplication
import com.myscript.iink.app.persistence.model.Content
import com.myscript.iink.app.persistence.model.IContentRepository

class RoomRepository : IContentRepository {
    private val dao = MyApplication.database.dao()

    override val contents: LiveData<List<Content>> = dao.contents

    override fun insert(vararg content: Content) {
        InsertAsyncTask(dao).execute(*content)
    }

    private class InsertAsyncTask(private val dao: IContentDao) : AsyncTask<Content, Void, Void>() {
        override fun doInBackground(vararg params: Content): Void? {
            this.dao.insert(*params)
            return null
        }
    }
}
