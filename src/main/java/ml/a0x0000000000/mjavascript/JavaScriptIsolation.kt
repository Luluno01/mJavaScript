package ml.a0x0000000000.mjavascript

interface JavaScriptIsolation<in T1, in T2, in T3> {
    fun exec(script: String, callback: T1?)
    fun execFile(path: String, callback: T2?)
    fun setOnResultCallback(callback: T3)
    fun destroy()
}