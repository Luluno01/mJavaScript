package ml.a0x00000000.mjavascript

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.nononsenseapps.filepicker.FilePickerActivity
import com.nononsenseapps.filepicker.Utils

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "mJS"
        private enum class REQUESTS {
            NEW_SCRIPT, OPEN_SCRIPT, NEW_PROJECT, OPEN_PROJECT
        }
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
    }

    private var exitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if(!Settings.isInit) {
            Settings.init(this)
        }

        recyclerView.addItemDecoration(RecyclerViewSpacesItemDecoration(null))
        FunctionItemInflater(this)
                .inflate(R.xml.main_functions, recyclerView)
                .adapter.listener = object: MainFunctionAdapter.OnItemClickListener {
            override fun onItemClick(index: Any) {
                this@MainActivity.onFunctionItemClick(index)
            }
        }
        Log.i(TAG, filesDir.path)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Snackbar.make(fab, R.string.exit_hint, Snackbar.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    private fun notImplement() {
        Snackbar.make(fab, R.string.under_construction, Snackbar.LENGTH_SHORT).show()
    }

    private fun newScript() {
        startActivity(Intent().setClass(this, EditActivity::class.java))
    }

    private fun openScript() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(intent, REQUESTS.OPEN_SCRIPT.ordinal)
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE)
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Settings.projectPath)
        startActivityForResult(intent, REQUESTS.OPEN_SCRIPT.ordinal)
    }

    private fun openProject() {
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)
        var initPath = Settings.projectPath
        if(ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            try {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            } catch(err: Exception) {
                initPath = Environment.getExternalStorageDirectory().canonicalPath
            }
        }
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, initPath)
        startActivityForResult(intent, MainActivity.Companion.REQUESTS.OPEN_PROJECT.ordinal)
    }

    private fun onPickFailed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_pick_failed_title)
                .setMessage(R.string.dialog_pick_failed_message)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                .create().show()
    }

    private fun onOpenFailed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_open_failed_title)
                .setMessage(R.string.dialog_open_failed_message)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                .create().show()
    }

    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
        when(req) {
            REQUESTS.OPEN_SCRIPT.ordinal -> {
                if(res == Activity.RESULT_OK && null != data) {
//                    val path = UriUtils.getPath(this, data.data)
                    val path = Utils.getFileForUri(Utils.getSelectedFilesFromResult(data)[0]).path
                    Log.i(TAG, "File selected: $path")
                    val intent = Intent()
                    intent.setClass(this, EditActivity::class.java)
                    intent.putExtra(EditActivity.EXTRA_PATH, path)
                    startActivity(intent)
                } else {
                    onPickFailed()
                }
            }
            REQUESTS.OPEN_PROJECT.ordinal -> {
                if(res == Activity.RESULT_OK && null != data) {
                    try {
                        val dir = Utils.getSelectedFilesFromResult(data)[0]
                        Log.i(TAG, "Folder selected: ${dir.path}")
                        Settings.projectPath = dir.path
                        Snackbar.make(fab, R.string.project_opened, Snackbar.LENGTH_SHORT).show()
                    } catch(err: NullPointerException) {
                        onOpenFailed()
                    } catch(err: IndexOutOfBoundsException) {
                        onOpenFailed()
                    }
                } else if(res != FilePickerActivity.RESULT_CANCELED) {
                    onOpenFailed()
                }
            }
        }
    }

    fun onFunctionItemClick(index: Any) {
        Log.i(TAG, "Function ${index as String} clicked")
        when(index) {
            "newScript" -> newScript()
            "openScript" -> openScript()
            "newProject" -> notImplement()
            "openProject" -> openProject()
        }
    }

    fun onFabClick(view: View) {
        newScript()
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
            R.id.action_settings -> {
                Settings.startSettingsActivity(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
