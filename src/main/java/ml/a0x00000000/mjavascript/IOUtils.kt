package ml.a0x00000000.mjavascript

import java.io.InputStream
import java.io.OutputStream

object IOUtils {
    /**
     * Read all text from `inputStream`.
     * *Note*:  It is the caller's responsibility to close the stream.
     * @return Corresponding content.
     */
    fun readText(inputStream: InputStream): String {
        return inputStream.bufferedReader().readText()
    }

    /**
     * Write content to `outputStream`.
     * *Note*:  It is the caller's responsibility to close the stream.
     */
    fun writeText(outputStream: OutputStream, content: String) {
        outputStream.bufferedWriter().write(content)
    }
}