package ml.a0x0000000000.mjavascript

import android.content.Context
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebView

class WebViewJavaScript constructor(context: Context): JavaScriptIsolation<ValueCallback<String>, ValueCallback<String>, (ConsoleMessage) -> Unit> {
    companion object {
        const val TAG: String = "WebViewJavaScript"
        class WebChromeClient constructor(): android.webkit.WebChromeClient() {
            var onMessageCallback: ((ConsoleMessage) -> Unit)? = null

            /**
             * Handles console message from WebView.
             * Message details could be accessed via the follows.
             * consoleMessage.message(): String
             * consoleMessage.lineNumber(): int
             * consoleMessage.sourceId(): String
             * consoleMessage.messageLevel(): ConsoleMessage.MessageLevel
             * @author Untitled
             * @param consoleMessage Message from console.
             * @return I don't know what this is for.
             */
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                onMessageCallback?.invoke(consoleMessage)
                Log.i(TAG, "Console message retrieved")
                super.onConsoleMessage(consoleMessage)
                return true  // Returning false will result in a extremely weird behavior
            }
        }
    }

    private var webView: WebView = WebView(context)

    init {
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
    }

    /**
     * Execute script.
     * @param script JavaScript string to be executed.
     * @param callback Function that handles the result of the execution.
     */
    override fun exec(script: String, callback: ValueCallback<String>?) {
        webView.evaluateJavascript(script, callback ?: ValueCallback { /* Empty callback */ })
    }

    /**
     * Load a new html file (may include `script` tags).
     * @param path Path to the target html file.
     * @param callback Dummy function as we just load an html file instead of js file.
     */
    override fun execFile(path: String, callback: ValueCallback<String>?) {
        webView.loadUrl(path)
    }

    /**
     * Set callback function for handling messages from console.
     * @param callback Callback function.
     */
    override fun setOnResultCallback(callback: (ConsoleMessage) -> Unit) {
        (webView.webChromeClient as WebChromeClient).onMessageCallback = callback
    }

    override fun destroy() {
        webView.removeAllViews()
        webView.destroy()
    }
}