/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.app.editors.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.myscript.iink.ContentPackage
import com.myscript.iink.ContentPart
import com.myscript.iink.Editor
import com.myscript.iink.IEditorListener
import com.myscript.iink.app.common.IInteractiveInkApplication
import com.myscript.iink.app.editors.MyApplication
import com.myscript.iink.app.editors.R
import com.myscript.iink.extensions.convert
import com.myscript.iink.uireferenceimplementation.EditorView
import com.myscript.iink.uireferenceimplementation.FontUtils
import com.myscript.iink.uireferenceimplementation.InputController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.problem_solver.view.*

class MainActivity : AppCompatActivity(), IEditorListener {

    private lateinit var answerContentPart1: ContentPart
    private lateinit var answerContentPart2: ContentPart
    private lateinit var scoreContentPart1: ContentPart
    private lateinit var scoreContentPart2: ContentPart
    private val allContentParts
        get() = listOf(
            answerContentPart1, answerContentPart2,
            scoreContentPart1, scoreContentPart2
        )
    private lateinit var answerEditorView1: EditorView
    private lateinit var scoreEditorView1: EditorView
    private lateinit var answerEditorView2: EditorView
    private lateinit var scoreEditorView2: EditorView
    private val allEditorViews: List<EditorView>
        get() = listOf(
            answerEditorView1, answerEditorView2,
            scoreEditorView1, scoreEditorView2
        )
    private var currentEditorView: EditorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: 1 - initialize content part with content package.
        (application as? MyApplication)?.contentPackage?.let { initWith(it) }
        // TODO: 2 - initialize with editor view.
        answerEditorView1 = problemSolver1.answerEditor.findViewById(R.id.editor_view)
        scoreEditorView1 = problemSolver1.scoreEditor.findViewById(R.id.editor_view)
        answerEditorView2 = problemSolver2.answerEditor.findViewById(R.id.editor_view)
        scoreEditorView2 = problemSolver2.scoreEditor.findViewById(R.id.editor_view)
        allEditorViews.forEach { initWith(it) }
    }

    private fun initWith(contentPackage: ContentPackage) = with(contentPackage) {
        // TODO: 3 - try different part types: Diagram, Drawing, Math, Text, Text Document.
        createPart("Math").let { answerContentPart1 = it }
        createPart("Text").let { answerContentPart2 = it }
        createPart("Text").let { scoreContentPart1 = it }
        createPart("Text").let { scoreContentPart2 = it }
    }

    private fun initWith(view: EditorView) = with(view) {
        (application as? IInteractiveInkApplication)?.engine?.let {
            setEngine(it)
            // TODO: 4 - try different input mode:
            // - InputController.INPUT_MODE_AUTO
            // - InputController.INPUT_MODE_NONE
            // - InputController.INPUT_MODE_FORCE_PEN
            // - InputController.INPUT_MODE_FORCE_TOUCH
            editor?.run {
                configuration.run {
                    when (this@with /* view */) {
                        answerEditorView1 -> setBoolean("math.solver.enable", false)
                        answerEditorView2,
                        scoreEditorView1, scoreEditorView2 -> setBoolean(
                            "text.guides.enable",
                            false
                        )
                    }
                    addListener(this@MainActivity)
                }
            }
            inputMode = InputController.INPUT_MODE_AUTO
            // uncomment the following line to use integrated font for math equations rendering.
            setTypefaces(FontUtils.loadFontsFromAssets(applicationContext.assets))
            post {
                // TODO: 5 - attach the content part to the editor.
                editor?.part = when (this /* view */) {
                    answerEditorView1 -> answerContentPart1
                    answerEditorView2 -> answerContentPart2
                    scoreEditorView1 -> scoreContentPart1
                    scoreEditorView2 -> scoreContentPart2
                    else -> null
                }
                visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        allEditorViews.forEach { it.close() }
        allContentParts.forEach { it.close() }
        super.onDestroy()
    }

    // region Implementations (options menu)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            currentEditorView?.editor?.run {
                it.findItem(R.id.menu_clear)?.isEnabled = part?.isClosed == false
                it.findItem(R.id.menu_redo)?.isEnabled = canRedo()
                it.findItem(R.id.menu_undo)?.isEnabled = canUndo()
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val editor = currentEditorView?.editor
        val editors = allEditorViews.map { it.editor }
        when (item?.itemId) {
            // TODO: 6 - clear and convert your contents.
            R.id.menu_clear -> editor?.clear()
            R.id.menu_clear_all -> editors.forEach { it?.clear() }
            R.id.menu_convert -> editor?.convert()
            R.id.menu_convert_all -> editors.forEach { it?.convert() }
            // TODO: 7 - redo and undo your modifications.
            R.id.menu_redo -> editor?.redo()
            R.id.menu_redo_all -> editors.forEach { it?.redo() }
            R.id.menu_undo -> editor?.undo()
            R.id.menu_undo_all -> editors.forEach { it?.undo() }
        }
        return super.onOptionsItemSelected(item)
    }

    // endregion

    // region Implementations (IEditorListener)

    override fun contentChanged(editor: Editor?, blockIds: Array<out String>?) {
        invalidateOptionsMenu()
        currentEditorView = allEditorViews.singleOrNull { it.editor == editor }
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
