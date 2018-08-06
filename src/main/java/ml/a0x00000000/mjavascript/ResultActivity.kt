package ml.a0x00000000.mjavascript

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_result.*
import ml.a0x00000000.mjavascript.editor.IridiumHighlightingEditorJ

class ResultActivity : AppCompatActivity(), IridiumHighlightingEditorJ.OnTextChangedListener {

    companion object {
        const val TAG: String = "ResultActivity"
    }

    private lateinit var editor: IridiumHighlightingEditorJ
    private lateinit var editActivityFragment: EditActivityFragment
    lateinit var engine: Engines
    private var webViewJavaScript: WebViewJavaScript? = null
    private var nodeJavaScript: NodeJavaScript? = null
    private lateinit var execution: Execution

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editActivityFragment = (supportFragmentManager.findFragmentById(R.id.fragment) as EditActivityFragment)
        editor = editActivityFragment.editor

        engine = Engines.values()[intent.getIntExtra("engine", Engines.WEB_VIEW.ordinal)]
        execution = object: Execution {
            override var filename = intent.getStringExtra("filename")!!
            override var script = intent.getStringExtra("script")!!
            override var workingDirectory = intent.getStringExtra("workingDirectory")!!
        }
        when(engine) {
            Engines.WEB_VIEW -> { initWebViewJavaScript(); runWebViewJavaScript(execution); }
            Engines.NODE -> {
                initNodeJavaScript()
                if(NodeJavaScript.getReady()) runNodeJavaScript(execution)
                else nodeJavaScript!!.onReadyCallback = NodeJavaScript.OnReadyListener {
                    Log.i(TAG, "NodeJavaScript reports ready, running script...")
                    runNodeJavaScript(execution)
                }
            }
        }
    }

    fun onFabClick(view: View) {
        rerun()
        Snackbar.make(view, R.string.rerun_hint, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    override fun onTextChanged(text: String) {}

    @SuppressLint("SetTextI18n")
    private fun initWebViewJavaScript() {
        webViewJavaScript = WebViewJavaScript(this)
        webViewJavaScript!!.setOnResultCallback { consoleMessage -> editor.setText("${editor.text}${consoleMessage.message()}\n") }
    }

    private fun runWebViewJavaScript(execution: Execution) {
        webViewJavaScript!!.exec(execution, null)
    }

    @SuppressLint("SetTextI18n")
    private fun initNodeJavaScript() {
        nodeJavaScript = NodeJavaScript(this)
        Log.i(TAG, "Setting onResultCallback")
        nodeJavaScript!!.setOnResultCallback(NodeJavaScript.OnResultListener { s ->
            editor.post {
                editor.setText("${editor.text}$s")  // Doesn't need to append newline
            }
        })
    }

    private fun runNodeJavaScript(execution: Execution) {
        Log.i(TAG, "Running script. ${nodeJavaScript!!.onResultCallback}")
        nodeJavaScript!!.exec(execution, NodeJavaScript.ExecutionListener { err, res ->
            if(null != err) {
                fab.post { onRunFailed(res) }
            }
        })
    }

    private fun onRunFailed(errMsg: String?) {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_run_failed_title)
                .setMessage(getString(R.string.dialog_run_failed_message).format(errMsg?:""))
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                .create().show()
    }

    override fun onDestroy() {
        webViewJavaScript?.setOnResultCallback(null)
        webViewJavaScript?.destroy()
        nodeJavaScript?.setOnResultCallback(null)
        super.onDestroy()
    }

    private fun rerun() {
        editor.setText("")
        when(engine) {
            Engines.WEB_VIEW -> runWebViewJavaScript(execution)
            Engines.NODE -> runNodeJavaScript(execution)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when(id) {
            android.R.id.home -> { finish(); return true; }
        }

        return super.onOptionsItemSelected(item)
    }
}
