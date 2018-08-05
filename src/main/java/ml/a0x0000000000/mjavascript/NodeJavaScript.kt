package ml.a0x0000000000.mjavascript

import android.content.Context
import android.util.Log
import android.webkit.ValueCallback
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class NodeJavaScript constructor(val context: Context): JavaScriptIsolation<ValueCallback<String>, ValueCallback<String>, (String) -> Unit> {
    companion object {
//        const val FILE_NAME: String = "NodeJavaScript"
        const val TAG: String = "NodeJavaScript"
        var nodeThread: Thread? = null
        var port: Int? = null

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("node")
        }

        private fun copyAssets(context: Context, assetsName: String, to: String) {
            try {
                val files: Array<String> = context.assets.list(assetsName)
                if(files.isNotEmpty()) {
                    // Is a directory
                    val file = File(to)
                    file.mkdirs()
                    for(filename in files) {
                        copyAssets(context, "$assetsName/$filename", "$to/$filename")
                    }
                } else {
                    // Is a file
                    val inputStream = context.assets.open(assetsName)
                    val fileOutputStream = FileOutputStream(File(to))
                    val buffer = ByteArray(1024)
                    var count = inputStream.read(buffer)
                    while(count != -1) {
                        fileOutputStream.write(buffer, 0, count)
                        count = inputStream.read(buffer)
                    }
                    fileOutputStream.flush()
                    inputStream.close()
                    fileOutputStream.close()
                }
            } catch (err: Exception) {
                Log.e(TAG, Log.getStackTraceString(err))
            }
        }
    }

    private var _onMessageCallback: ((String) -> Unit)? = null
    var onMessageCallback: ((String) -> Unit)?
    get() {
        return _onMessageCallback
    }
    set(value) {
        if(null == _onMessageCallback && null == port) {
            _onMessageCallback = { _port -> port = _port.trim().toInt(); Log.i(TAG, "Target port: $port"); _onMessageCallback = value; onReadyCallback?.invoke() }
        } else {
            _onMessageCallback = value
        }
    }

    var onReadyCallback: (() -> Unit)? = null

    init {
        copyAssets(context, "js", "${context.filesDir}/js")
        if(null == nodeThread) {
            nodeThread = Thread(
                    Runnable {
                        var res = 0
                        try {
                            res = startNodeWithArguments(
                                    arrayOf(
                                            "node",
                                            "${context.filesDir}/js/node-server.js"
                                    )
                            )
                        } catch(err: InterruptedException) {
                            Log.i(TAG, "Node.js was force closed")
                        }
                        Log.i(TAG, "Node.js exited with code $res")
                        nodeThread = null
                    }
            )
            nodeThread!!.start()
            Log.i(TAG, "Node.js instance running")
        } else {
            Log.w(TAG, "Only one Node.js instance can be there at the same time")
        }
    }

    private fun onResult(text: String) {
        Log.i(TAG, "Incoming message: $text")
        onMessageCallback?.invoke(text)
    }

    /**
     * Start a Node.js instance with arguments.
     * @param args Arguments to be passed to Node.js instance.
     * @return Exit status of Node.js instance.
     */
    private external fun startNodeWithArguments(args: Array<String>): Int

    override fun exec(script: String, callback: ValueCallback<String>?) {
//        val file: File = createTempFile(FILE_NAME + System.currentTimeMillis(), ".js")
//        file.deleteOnExit()
//        file.writeText(script)
        val url = URL("http://127.0.0.1:$port")
        val connection: HttpURLConnection?
        val res: String
        val reader: BufferedReader
        val writer: BufferedWriter
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true
            connection.useCaches = false
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.readTimeout = 10000  // 10s
            connection.connectTimeout = 10000  // 10s
            connection.connect()

            val obj = JSONObject()
            obj.put("filename", "mJavaScript.js")
            obj.put("script", script)
            obj.put("workingDirectory", "${context.filesDir}")
            writer = connection.outputStream.bufferedWriter()
            writer.write(obj.toString())
            writer.close()

            reader = connection.inputStream.bufferedReader()
            res = reader.readText()
            Log.i(TAG, "Response: $res")
            reader.close()
            if(connection.responseCode != 200) {
                throw Exception(res)
            }
        } catch(err: Exception) {
            Log.e(TAG, "Cannot execute script: ${Log.getStackTraceString(err)}")
            throw Exception("Cannot execute script", err)
        }
    }

    override fun execFile(path: String, callback: ValueCallback<String>?) {
        TODO("NOT IMPLEMENTED")
    }

    override fun setOnResultCallback(callback: (String) -> Unit) {
        onMessageCallback = callback
    }

    override fun destroy() {
        nodeThread?.interrupt()
        nodeThread = null
    }
}