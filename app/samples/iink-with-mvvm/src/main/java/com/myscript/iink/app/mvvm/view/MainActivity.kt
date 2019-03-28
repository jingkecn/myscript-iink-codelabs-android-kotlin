/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.mvvm.view

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
import com.myscript.iink.app.mvvm.MyApplication
import com.myscript.iink.app.mvvm.R
import com.myscript.iink.app.mvvm.viewmodel.ContentViewModel
import com.myscript.iink.extensions.convert
import com.myscript.iink.extensions.parts
import com.myscript.iink.uireferenceimplementation.EditorView
import com.myscript.iink.uireferenceimplementation.InputController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        content.observe(this@MainActivity, Observer { content ->
            content?.let {
                editorView.run {
                    post {
                        contentPackage?.parts
                            ?.singleOrNull { it.id == content.contentPart }
                            ?.let { editor?.part = it }
                        visibility = View.VISIBLE
                    }
                }
            }
        })
        // initialize a content part in view model.
        // TODO: 3 - try different part types: Diagram, Drawing, Math, Text, Text Document.
        contentPart = contentPart ?: contentPackage?.createPart("Text Document")
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
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    // endregion
}
