package ml.a0x00000000.mjavascript;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NodeJavaScript implements JavaScriptIsolation {
    public interface OnResultListener {
        void onResult(String text);
    }
    public interface OnReadyListener {
        void onReady();
    }
    public interface ExecutionListener {
        void onExecuted(@Nullable Exception err, @Nullable String res);
    }
    static public final String TAG = "NodeJavaScript";
    static public Thread nodeThread = null;
    static public int port = -1;
    static public boolean getReady() {
        return port != -1;
    }
    static private void setReady(boolean value) throws Exception {
        throw new Exception("Setter not available");
    }
    static private OnResultListener onResultCallback = null;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("node");
    }

    static private void copyAssets(Context context, String assetsName, String to) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            String[] files = context.getAssets().list(assetsName);
            if(files.length > 0) {
                // Is a directory
                File file = new File(to);
                file.mkdirs();
                for (String filename : files) {
                    copyAssets(context, assetsName + "/" + filename, to + "/" + filename);
                }
            } else {
                // Is a file
                inputStream = context.getAssets().open(assetsName);
                fileOutputStream = new FileOutputStream(new File(to));
                byte[] buffer = new byte[1024];
                int count;
                while((count = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.flush();
                inputStream.close();
                fileOutputStream.close();
            }
        } catch (Exception err) {
            Log.e(TAG, "Ignored error: " + Log.getStackTraceString(err));
        } finally {
            if(null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception err) {
                    Log.e(TAG, "Ignored error: " + Log.getStackTraceString(err));
                }
            }
            if(null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (Exception err) {
                    Log.e(TAG, "Ignored error: " + Log.getStackTraceString(err));
                }
            }
        }
    }

    public OnReadyListener onReadyCallback = null;
    private Context context = null;

    NodeJavaScript(Context context) {
        this.context = context;
        final String filesDir = context.getFilesDir().getAbsolutePath();
        copyAssets(context, "js", filesDir + "/js");
        if(null == nodeThread) {
            nodeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int res = -1;
                    try {
                        res = startNodeWithArguments(new String[] {"node", filesDir + "/js/node-server.js" });
                    } catch(Exception err) {
                        Log.i(TAG, "Node.js was force closed");
                    }
                    Log.i(TAG, "Node.js exited with code " + res);
                    nodeThread = null;
                }
            });
            nodeThread.start();
            Log.i(TAG, "Node.js instance running");
        } else {
            Log.w(TAG, "Only one Node.js instance can be there at the same time");
        }
    }

    /**
     * Start a Node.js instance with arguments.
     * @param args Arguments to be passed to Node.js instance.
     * @return Exit status of Node.js instance.
     */
    native private int startNodeWithArguments(String[] args);

    @Override
    public void exec(@NotNull Execution _execution, @Nullable final Object callback) {
        NodeJavaScriptHelper.INSTANCE.exec(_execution, (ExecutionListener) callback);
    }

    @Override
    public void execFile(@NotNull String path, @org.jetbrains.annotations.Nullable Object callback) {
        // TODO: Not Implemented
    }

    @Override
    public void destroy() {
        if(null != nodeThread) {
            nodeThread.interrupt();
        }
        nodeThread = null;
    }

    /**
     * Handler function called by native code.
     * @param text String from Node.js script server.
     */
    static private void onResult(String text) {
        Log.v(TAG, "Incoming message: "+ text);
        if(null != onResultCallback) onResultCallback.onResult(text);
    }

    OnResultListener getOnResultCallback() {
        return onResultCallback;
    }

    /**
     * Set onResultCallback.
     * NOT THREAD-SAFE!
     * @param callback Callback function that handles returned result from Node.js server.
     */
    @Override
    public void setOnResultCallback(final Object callback) {
        if(null == onResultCallback && port == -1) {
            Log.i(TAG, "Setting shim callback");
            onResultCallback = new OnResultListener() {
                @Override
                public void onResult(String _port) {
                    Log.d(TAG, "In shim callback");
                    port = Integer.valueOf(_port.trim());
                    Log.i(TAG, "Target port: "+ port);
                    onResultCallback = (OnResultListener) callback;
                    Log.d(TAG, "onReadyCallback: " + onReadyCallback);
                    if(null != onReadyCallback) onReadyCallback.onReady();
                }
            };
        } else {
            onResultCallback = (OnResultListener) callback;
        }
    }
}
