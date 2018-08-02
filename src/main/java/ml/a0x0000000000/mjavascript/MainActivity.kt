package ml.a0x0000000000.mjavascript

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private var started: Boolean = false

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("node")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        if (!started) {
            started = true
            Thread(Runnable {
                startNodeWithArguments(arrayOf("node", "-e", "let http = require('http');" +
                        "let server = http.createServer((request, response) => {" +
                        "  response.end('Versions: ' + JSON.stringify(process.versions));" +
                        "});" +
                        "server.listen(3000);"))
            }).start()
        }
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    /**
     * Start a Node.js instance with arguments.
     * @param {Array<String>} args Arguments to be passed to Node.js instance.
     * @return {Integer} Exit status of Node.js instance.
     */
    external fun startNodeWithArguments(args: Array<String>): Integer
}
