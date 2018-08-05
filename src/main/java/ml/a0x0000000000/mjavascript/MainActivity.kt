package ml.a0x0000000000.mjavascript

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "mJS"

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
//            System.loadLibrary("node")
        }
    }

    private var webViewJavaScript: WebViewJavaScript? = null
    private var nodeJavaScript: NodeJavaScript? = null
    private var exitTime: Long = 0
    private lateinit var adapter: MainFunctionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        recyclerView.addItemDecoration(RecyclerViewSpacesItemDecoration(null))
        FunctionItemInflater(this)
                .inflate(R.xml.main_functions, recyclerView)
                .adapter.listener = object: MainFunctionAdapter.OnItemClickListener {
            override fun onItemClick(index: Any) {
                this@MainActivity.onFunctionItemClick(index)
            }
        }
//        sample_text.text = ""
//
//        webViewJavaScript = WebViewJavaScript(this)
//        webViewJavaScript.exec("setTimeout(() => console.log(new Error('Rua!')), 5000)", null)
//        webViewJavaScript.setOnResultCallback { message -> onResult("${message.message()}\n") }
//
//        nodeJavaScript = NodeJavaScript(this)
//        nodeJavaScript.onMessageCallback = { text -> onResult(text) }
//        nodeJavaScript.onReadyCallback = { nodeJavaScript.exec("console.log('Rua!');", null) }
//
//        Log.i(TAG, "$filesDir")
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Snackbar.make(fab, getString(R.string.exit_hint), Snackbar.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    private fun notImplement() {
        Snackbar.make(fab, getString(R.string.under_construction), Snackbar.LENGTH_SHORT).show()
    }

    fun onFunctionItemClick(index: Any) {
        Log.i(TAG, "Function ${index as String} clicked")
        when(index) {
            "openScript" -> notImplement()
            "newProject" -> notImplement()
            "openProject" -> notImplement()
        }
    }

    fun onFabClick(view: View) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    override fun onDestroy() {
        webViewJavaScript?.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
