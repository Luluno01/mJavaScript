package ml.a0x00000000.mjavascript.highlightingdefinitions

import ml.a0x00000000.mjavascript.editor.HighlightingDefinition
import ml.a0x00000000.mjavascript.highlightingdefinitions.definitions.*

/**
 * Author: 0xFireball
 */
class HighlightingDefinitionLoader {

    fun selectDefinitionFromFileExtension(selectedFileExt: String): HighlightingDefinition {
        return when (selectedFileExt) {
            "ts" -> JavaScriptHighlightingDefinition()  // Temporary solution
            "js" -> JavaScriptHighlightingDefinition()
            "java" -> JavaHighlightingDefinition()
            "cs" -> CSharpHighlightingDefinition()
            "cpp", "cxx" -> CPlusPlusHighlightingDefinition()
            "lua" -> LuaHighlightingDefinition()
            // "py" -> return PythonHighlightingDefinition() //Not yet ready!
            "txt" -> NoHighlightingDefinition()
            else -> {
                GenericHighlightingDefinition()
            }
        }
    }
}
