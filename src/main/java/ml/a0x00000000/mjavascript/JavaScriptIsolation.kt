package ml.a0x00000000.mjavascript

interface JavaScriptIsolation<in T1, in T2, in T3> {
    fun exec(execution: Execution, callback: T1?)
    fun execFile(path: String, callback: T2?)
    fun setOnResultCallback(callback: T3)
    fun destroy()
}

interface Execution {
    var filename: String
    var script: String
    var workingDirectory: String
}