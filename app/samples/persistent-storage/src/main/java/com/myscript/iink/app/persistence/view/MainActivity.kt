/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.persistence.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.myscript.iink.ContentPart
import com.myscript.iink.Editor
import com.myscript.iink.IEditorListener
import com.myscript.iink.app.common.IInteractiveInkApplication
import com.myscript.iink.app.persistence.Constants.IINK_PACKAGE_NAME
import com.myscript.iink.app.persistence.MyApplication
import com.myscript.iink.app.persistence.R
import com.myscript.iink.app.persistence.viewmodel.ContentViewModel
import com.myscript.iink.extensions.convert
import com.myscript.iink.extensions.parts
import com.myscript.iink.uireferenceimplementation.EditorView
import com.myscript.iink.uireferenceimplementation.InputController

class MainActivity : AppCompatActivity(), IEditorListener {

    private val contentPackage get() = (application as? MyApplication)?.contentPackage
    private lateinit var editorView: EditorView
    private lateinit var vm: ContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: 1 - initialize with editor view.
        editorView = findViewById<EditorView>(R.id.editor_view).also { initWith(it) }
        // TODO: 2 - initialize content part with content view model.
        vm = ViewModelProviders.of(this).get(ContentViewModel::class.java)
            .also { initWith(it) }
    }

    private fun initWith(vm: ContentViewModel) = with(vm) {
        // observes content changes:
        // - if content package has a content part stored in view model
        // - - get the content part from content package by id and attach it to the editor
        // - otherwise: create a new content part and attach it to the editor.
        contents.observe(this@MainActivity, Observer { contents ->
            val content = contents?.singleOrNull { it.contentPackage == IINK_PACKAGE_NAME }
            // initializes content part.
            // TODO: 3 - try different part types: Diagram, Drawing, Math, Text, Text Document.
            contentPart = contentPart // content part stored in view model.
                ?: content?.contentPart?.let {
                    // content part from content package by id stored in model repository.
                    contentPackage?.parts?.singleOrNull { part -> part.id == it }
                } ?: contentPackage?.createPart("Text Document") // create a new content part.
            // TODO: 5 - attach the content part to the editor.
            editorView.run {
                post {
                    contentPart?.let { editor?.part = it }
                    visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initWith(view: EditorView) = with(view) {
        (application as? IInteractiveInkApplication)?.engine?.let {
            setEngine(it)
            editor?.addListener(this@MainActivity)
            // TODO: 4 - try different input mode:
            // - InputController.INPUT_MODE_AUTO
            // - InputController.INPUT_MODE_NONE
            // - InputController.INPUT_MODE_FORCE_PEN
            // - InputController.INPUT_MODE_FORCE_TOUCH
            inputMode = InputController.INPUT_MODE_AUTO
            // uncomment the following line to use integrated font for math equations rendering.
            // setTypefaces(FontUtils.loadFontsFromAssets(applicationContext.assets))
        }
    }

    override fun onStop() {
        contentPackage?.saveToTemp()
        super.onStop()
    }

    override fun onDestroy() {
        editorView.close()
        super.onDestroy()
    }

    // region Implementations (options menu)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            editorView.editor?.run {
                it.findItem(R.id.menu_clear)?.isEnabled = part?.isClosed == false
                it.findItem(R.id.menu_redo)?.isEnabled = canRedo()
                it.findItem(R.id.menu_undo)?.isEnabled = canUndo()
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        editorView.editor?.let {
            // wait for the editor to be idle.
            if (!it.isIdle) it.waitForIdle()
            when (item?.itemId) {
                // TODO: 6 - clear and convert your contents.
                R.id.menu_clear -> it.clear()
                R.id.menu_convert -> it.convert()
                // TODO: 7 - redo and undo your modifications.
                R.id.menu_redo -> it.redo()
                R.id.menu_undo -> it.undo()
                else -> return@let
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // endregion

    // region Implementations (IEditorListener)

    override fun contentChanged(editor: Editor?, blockIds: Array<out String>?) {
        invalidateOptionsMenu()
    }

    override fun partChanging(editor: Editor?, old: ContentPart?, new: ContentPart?) {
        invalidateOptionsMenu()
    }

    override fun partChanged(editor: Editor?) {
        invalidateOptionsMenu()
    }

    override fun onError(editor: Editor?, blockId: String?, message: String?) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    // endregion
}
