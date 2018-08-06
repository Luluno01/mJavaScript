package ml.a0x00000000.mjavascript

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.HttpURLConnection
import java.net.URL

object NodeJavaScriptHelper {
    const val TAG: String = "NodeJavaScriptHelper"

    @Suppress("UNCHECKED_CAST")
    fun exec(execution: Execution, callback: NodeJavaScript.ExecutionListener?) {
//        val file: File = createTempFile(FILE_NAME + System.currentTimeMillis(), ".js")
//        file.deleteOnExit()
//        file.writeText(script)
        Thread(Runnable {
            val url = URL("http://127.0.0.1:${NodeJavaScript.port}")
            val connection: HttpURLConnection?
            var res: String? = null
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
                obj.put("filename", execution.filename)
                obj.put("script", execution.script)
                obj.put("workingDirectory", execution.workingDirectory)
                writer = connection.outputStream.bufferedWriter()
                writer.write(obj.toString())
                writer.close()

                reader = if(connection.responseCode >= 400) {
                    connection.errorStream.bufferedReader()
                } else {
                    connection.inputStream.bufferedReader()
                }
                res = reader.readText()
                Log.i(TAG, "Response: $res")
                reader.close()
                if(connection.responseCode != 200) {
                    callback?.onExecuted(Exception("Cannot execute script"), res)
                } else {
                    callback?.onExecuted(null, null)
                }
            } catch(err: Exception) {
                Log.e(TAG, "Cannot execute script: ${Log.getStackTraceString(err)}")
                callback?.onExecuted(Exception("Cannot execute script", err), res)
            }
        }).start()
    }
}