package ml.a0x00000000.mjavascript

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.nononsenseapps.filepicker.FilePickerActivity
import com.nononsenseapps.filepicker.Utils

import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*
import ml.a0x00000000.mjavascript.editor.IridiumHighlightingEditorJ
import ml.a0x00000000.mjavascript.highlightingdefinitions.HighlightingDefinitionLoader
import ml.a0x00000000.mjavascript.highlightingdefinitions.definitions.JavaScriptHighlightingDefinition
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException


open class EditActivity : AppCompatActivity(), IridiumHighlightingEditorJ.OnTextChangedListener {
    companion object {
        const val TAG: String = "EditActivity"
        const val EXTRA_PATH: String = "path"
        private enum class REQUESTS {
            SAVE_AS
        }
        const val TAB_REPLACE_COUNT: Int = 2

        /**
         * Temporary solution.
         */
        protected open class TabFix(val editor: IridiumHighlightingEditorJ): TextWatcher {
            protected var replacer: String
            protected var start: Int = 0
            protected var count: Int = 0
            init {
                val builder = StringBuilder()
                for (i in 0 until TAB_REPLACE_COUNT) {
                    builder.append(" ")
                }
                replacer = builder.toString()
            }
            protected lateinit var changedText: CharSequence
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                val reg = Regex("\t")
                changedText = if(reg.containsMatchIn(p0!!)) {
                    Log.d(TAG, "Tab found")
                    p0.subSequence(start, start + count).replace(Regex("\t"), replacer)
                } else ""
                this.start = start
                this.count = count
            }

            override fun afterTextChanged(e: Editable?) {
                if(!changedText.isEmpty()) e!!.replace(start, start + count, changedText)
            }
        }
    }

    private var currentFile: String? = null
    private lateinit var editor: IridiumHighlightingEditorJ
    private lateinit var editActivityFragment: EditActivityFragment
    private var isFirstChangeAfterLoaded: Boolean = true
    private var _saved: Boolean = true
    private var saved: Boolean
    get() = _saved
    set(value) {
        if(isFirstChangeAfterLoaded && !value && null != currentFile) {
            isFirstChangeAfterLoaded = false
        } else {
            _saved = value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!Settings.isInit) {
            Settings.init(this)
        }
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        editActivityFragment = (supportFragmentManager.findFragmentById(R.id.fragment) as EditActivityFragment)
        editor = editActivityFragment.editor
        val path = intent.getStringExtra(EXTRA_PATH)
        if(null != path) {
            currentFile = path
            try {
                editor.addTextChangedListener(TabFix(editor))
                editor.setText(readFile(path))
                filename.text = path
                line_number.text = getString(R.string.line_count).format(editor.lineCount)
                Log.i(TAG, "File ($path) opened")
                Log.v(TAG, "Content: ${editActivityFragment.text}")
                editor.loadHighlightingDefinition(HighlightingDefinitionLoader().selectDefinitionFromFileExtension(File(path).extension))
                editor.updateHighlighting()
            } catch(err: IOException) {
                onOpenFailed()
            }
        } else {
            filename.text = Settings.defaultFilename
            currentFile = null
            editor.loadHighlightingDefinition(JavaScriptHighlightingDefinition())
        }
        saved = true
        editor.setTabWidth(2)
        line_number.text = getString(R.string.line_count).format(editor.lineCount)
    }

    @Throws(IOException::class)
    fun readFile(path: String): String {
        val reader = FileReader(path)
        try {
            val res = reader.readText()
            reader.close()
            return res
        } catch(err: IOException) {
            reader.close()
            throw err
        }
    }

    @Throws(IOException::class)
    fun writeFile(path: String, content: String) {
//        val out = BufferedWriter(OutputStreamWriter(
//                FileOutputStream(filePath), "UTF-8"))
//        out.write(contents)
//        out.close()
        val writer = FileWriter(path)
        try {
            writer.write(content)
            writer.close()
        } catch(err: IOException) {
            writer.close()
            throw err
        }
    }

    private fun onOpenFailed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_open_failed_title)
                .setMessage(R.string.dialog_open_failed_message)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> currentFile = null; finish(); }
                .setOnDismissListener { currentFile = null; finish(); }
                .create().show()
    }

    private fun save() {
        if(null != currentFile) {
            try {
                writeFile(currentFile!!, editor.cleanText)
                Snackbar.make(fab, R.string.save_success, Snackbar.LENGTH_SHORT).show()
                saved = true
            } catch (err: IOException) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_save_failed_title)
                        .setMessage(R.string.dialog_save_failed_message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                        .create().show()
            }
        } else saveAs()
    }

    /**
     * Trigger file selection.
     */
    private fun saveAs() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(fab.windowToken, 0)
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE_AND_DIR)
        val initPath = if(currentFile.equals(getString(R.string.default_filename)) || null == currentFile) Settings.projectPath else File(currentFile).parent
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, initPath)
        startActivityForResult(intent, REQUESTS.SAVE_AS.ordinal)
    }

    private fun saveFile(path: String) {
        try {
            writeFile(path, editor.cleanText)
            Snackbar.make(fab, R.string.save_success, Snackbar.LENGTH_SHORT).show()
            currentFile = path
            filename.postDelayed({
                filename.text = currentFile
            }, 500)
            saved = true
            editor.loadHighlightingDefinition(HighlightingDefinitionLoader().selectDefinitionFromFileExtension(File(currentFile).extension))
        } catch (err: IOException) {
            Log.d(TAG, path)
            AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_save_as_failed_title)
                    .setMessage(R.string.dialog_save_as_failed_message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                    .create().show()
        }
    }

    private fun overrideFile(path: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_override_title)
                .setMessage(getString(R.string.dialog_override_message).format(path))
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_confirm) { dialog, _ ->
                    dialog.dismiss()
                    saveFile(path)
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                .create().show()
    }

    /**
     * Real `saveAs` function.
     */
    private fun saveAs(data: Intent) {
        val file = Utils.getFileForUri(Utils.getSelectedFilesFromResult(data)[0])
        when {
            file.isFile -> {
                overrideFile(file.absolutePath)
            }
            file.isDirectory -> {
                val editText = EditText(this)

                val defaultFileName = Settings.defaultFilename
                editText.setText(defaultFileName)
                var index = defaultFileName.lastIndexOf('.')
                index = if(index < 0) 0 else index
                editText.setSelection(0, index)
                val alertDialog = ml.a0x00000000.mjavascript.helpers.AlertDialog(this)
                alertDialog.setTitle(R.string.dialog_save_as_filename_title)
                alertDialog.editText = editText
                alertDialog.setPositiveButton(R.string.dialog_confirm, null) { dialog, _ ->
                    if(editText.text.isBlank()) {
                        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                    } else {
                        dialog.dismiss()
                        alertDialog.hideSoftKeyboard()
                        var path = file.absolutePath
                        path = if (path.endsWith('/')) {
                            "$path${editText.text}"
                        } else {
                            "$path/${editText.text}"
                        }
                        val newFile = File(path)
                        if(newFile.exists()) overrideFile(path)
                        else saveFile(path)
                    }
                }
                alertDialog.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                alertDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUESTS.SAVE_AS.ordinal) {
            if(resultCode == Activity.RESULT_OK) {
                saveAs(data!!)
            } else if(resultCode != FilePickerActivity.RESULT_CANCELED) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_save_as_pick_failed_title)
                        .setMessage(R.string.dialog_save_as_pick_failed_message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                        .create().show()
            }
        }
    }

    private fun goto() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(fab.windowToken, 0)
        val alertDialog = ml.a0x00000000.mjavascript.helpers.AlertDialog(this)
        alertDialog.setTitle(R.string.dialog_save_as_filename_title)
        val editText = EditText(this)
        editText.setHint(R.string.dialog_goto_hint)
        alertDialog.editText = editText
        alertDialog.setPositiveButton(R.string.dialog_confirm, null) { dialog, _ ->
            if(editText.text.isBlank()) {
                editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
            } else {
                try {
                    val data = editText.text.split(':')
                    val row = data[0].toInt()
                    var column = 0
                    try {
                        column = data[1].toInt()
                    } catch(err: NumberFormatException) {
                        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                        return@setPositiveButton
                    } catch(err: IndexOutOfBoundsException) {}
                    if(row < 0 || column < 0) {
                        editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                        return@setPositiveButton
                    }
                    if(!goto(row, column)) {
                        Snackbar.make(fab, R.string.position_not_found, Snackbar.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                } catch(err: NumberFormatException) {
                    editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                } catch(err: IndexOutOfBoundsException) {
                    editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                }
            }
        }
        alertDialog.setNegativeButton(R.string.dialog_cancel) { _, _ -> }
        alertDialog.show()
    }

    private fun goto(line: Int, column: Int): Boolean {
        var index = 0
        var mLine = 1
        var mColumn = 0
        for (c in editor.cleanText) {
            if(mLine == line && mColumn == column) {
                // Position found
                editor.setSelection(index)
                return true
            }
            if(mLine == line) mColumn++
            if(c == '\n') mLine++
            if(mLine > line) {
                // Column not found
                editor.setSelection(index)
                return true
            }
            index++
        }
        if(mLine == line) {
            // Last character
            editor.setSelection(index)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when(id) {
            android.R.id.home -> { return onReturn() }
            R.id.action_save -> save()
            R.id.action_save_as -> saveAs()
            R.id.action_go_to -> goto()
            R.id.action_settings -> Settings.startSettingsActivity(this)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onTextChanged(text: String) {
        line_number.text = getString(R.string.line_count).format(editor.lineCount)
        saved = false
        Log.v(TAG, "Text changed to $text")
    }

    @SuppressLint("ResourceType")
    @Suppress("UNUSED_PARAMETER")
    fun onFabClick(view: View) {
        val intent = Intent()
        intent.setClass(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_FILENAME, currentFile?:getString(R.string.default_filename))
        intent.putExtra(ResultActivity.EXTRA_SCRIPT, editor.cleanText)
        intent.putExtra(ResultActivity.EXTRA_WORKING_DIRECTORY, if(null != currentFile) File(currentFile).parentFile.absolutePath else filesDir.absolutePath)

        if(!Settings.neverAsk) {
            val engines = resources.obtainTypedArray(R.array.engines)
            val enginesNames: Array<String> = Array(engines.length()) { _ -> "" }
            val enginesDescription: Array<String> = Array(engines.length()) { _ -> "" }
            var selected = 0
            Log.i(TAG, engines.length().toString())
            for (i in 0 until engines.length()) {
                val resId = engines.getResourceId(i, -1)
                if (resId < 0) {
                    continue
                }
                val engine = resources.obtainTypedArray(resId)
                enginesNames[i] = engine.getString(0)
                enginesDescription[i] = engine.getString(1)
                if(enginesNames[i] == Settings.defaultEngine.getEngineName()) selected = i
                engine.recycle()
            }
            engines.recycle()
            AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_select_engine_title)
//                .setMessage(R.string.dialog_select_engine_message)
                    .setSingleChoiceItems(
                            enginesDescription,
                            selected
                    ) { _, which ->
                        selected = which
                    }
                    .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                        when (enginesNames[selected]) {
                            Engines.WEB_VIEW.getEngineName() -> intent.putExtra(ResultActivity.EXTRA_ENGINE, Engines.WEB_VIEW.ordinal)
                            Engines.NODE.getEngineName() -> intent.putExtra(ResultActivity.EXTRA_ENGINE, Engines.NODE.ordinal)
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
                    .create().show()
        } else {
            intent.putExtra(ResultActivity.EXTRA_ENGINE, Settings.defaultEngine.ordinal)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if(!onReturn()) super.onBackPressed()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun onReturn(): Boolean {
        if(saved) finish()
        else {
            AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unsaved_hint_title)
                    .setMessage(R.string.dialog_unsaved_hint_description)
                    .setPositiveButton(android.R.string.yes) { _, _ -> save() }
                    .setNegativeButton(R.string.dialog_unsaved_hint_do_not_save) { _, _ -> finish() }
                    .create().show()
        }
        return true  // Consume the click
    }

    override fun onPause() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(editor.windowToken, 0)
        super.onPause()
    }

}
