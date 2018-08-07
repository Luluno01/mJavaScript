package ml.a0x00000000.mjavascript

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.File

object Settings {
    const val PREFS_NAME: String = "mJavaScript"
    const val PREFS_PROJECT_PATH: String = "project_path"
    const val PREFS_DEFAULT_FILENAME: String = "default_filename"
    const val PREFS_NEVER_ASK: String = "never_ask"
    const val PREFS_DEFAULT_ENGINE: String = "default_engine"
    const val TAG: String = "Settings"
    lateinit var settings: SharedPreferences
    var isInit: Boolean = false
    private set

    private var _projectPath: String = Environment.getExternalStorageDirectory().absolutePath
    var projectPath: String
    get() = _projectPath
    set(newPath) {
        var mNewPath = newPath
        if(!File(newPath).canRead()) {
            Log.i(TAG, "Attempt to set an unreadable path: $newPath")
            mNewPath = newPath.replace(Regex("^/root"), "")
            if(!File(mNewPath).canRead()) {
                Log.i(TAG, "Still cannot read $newPath")
                mNewPath = Environment.getExternalStorageDirectory().absolutePath
            }
        }
        _projectPath = mNewPath
        writeStringSetting(PREFS_PROJECT_PATH, _projectPath)
        Log.d(TAG, "New project path: $projectPath")
    }
    fun setProjectPathFromSettingsActivity(newPath: String) {
        var mNewPath = newPath
        if(!File(newPath).canRead()) {
            Log.i(TAG, "Attempt to set an unreadable path: $newPath")
            mNewPath = newPath.replace(Regex("^/root"), "")
            if(!File(mNewPath).canRead()) {
                Log.i(TAG, "Still cannot read $newPath")
                mNewPath = Environment.getExternalStorageDirectory().absolutePath
            }
        }
        _projectPath = mNewPath
        Log.d(TAG, "New project path: $projectPath")
    }

    private lateinit var _defaultFilename: String
    var defaultFilename: String
    get() = _defaultFilename
    set(newName) {
        if(newName.isBlank()) return
        writeStringSetting(PREFS_DEFAULT_FILENAME, newName)
        Log.i(TAG, "New default_filename set: $newName")
        _defaultFilename = newName
    }
    fun setDefaultFilenameFromSettingsActivity(newName: String) {
        Log.i(TAG, "New default_filename set: $newName")
        _defaultFilename = newName
    }

    private var _neverAsk: Boolean = false
    var neverAsk: Boolean
    get() = _neverAsk
    set(value) {
        writeBooleanSetting(PREFS_NEVER_ASK, value)
        Log.i(TAG, "New never_ask set: $value")
        _neverAsk = value
    }

    private var _defaultEngine: Engines = Engines.WEB_VIEW
    var defaultEngine: Engines
    get() = _defaultEngine
    set(value) {
        writeStringSetting(PREFS_DEFAULT_ENGINE, value.getEngineName())
        _defaultEngine = value
    }

    fun init(context: Context) {
        if(!isInit) {
//            settings = context.getSharedPreferences(PREFS_NAME, 0)
            settings = PreferenceManager.getDefaultSharedPreferences(context)

            _projectPath = getStringSetting(PREFS_PROJECT_PATH, projectPath)
            Log.i(TAG, "project_path loaded: $_projectPath")
            _defaultFilename = getStringSetting(PREFS_DEFAULT_FILENAME, context.getString(R.string.default_filename))
            Log.i(TAG, "default_filename loaded: $_defaultFilename")
            _neverAsk = getBooleanSetting(PREFS_NEVER_ASK, _neverAsk)
            Log.i(TAG, "never_ask loaded: $_neverAsk")

            val engineName = getStringSetting(PREFS_DEFAULT_ENGINE, _defaultEngine.getEngineName())
            for(engine in Engines.values()) {
                if(engine.getEngineName() == engineName) {
                    _defaultEngine = engine
                    break
                }
            }
            Log.i(TAG, "default_engine loaded: $_defaultEngine")

            isInit = true
        }
    }

    fun startSettingsActivity(context: Context) {
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    private fun getStringSetting(name: String, default: String): String {
        return settings.getString(name, default)
    }

    private fun writeStringSetting(name: String, setting: String) {
        settings.edit().putString(name, setting).apply()
    }

    private fun getBooleanSetting(name: String, default: Boolean): Boolean {
        return settings.getBoolean(name, default)
    }

    private fun writeBooleanSetting(name: String, setting: Boolean) {
        settings.edit().putBoolean(name, setting).apply()
    }
}