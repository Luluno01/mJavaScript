package ml.a0x00000000.mjavascript

enum class Engines {
    WEB_VIEW {
        override fun getEngineName() = "WebView"
    }, NODE {
        override fun getEngineName() = "Node"
    };
    abstract fun getEngineName(): String
}