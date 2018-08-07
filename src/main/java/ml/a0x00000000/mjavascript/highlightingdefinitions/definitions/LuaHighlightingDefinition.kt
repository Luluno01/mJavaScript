package ml.a0x00000000.mjavascript.highlightingdefinitions.definitions

import ml.a0x00000000.mjavascript.editor.HighlightingDefinition
import java.util.regex.Pattern

/**
 * Author: 0xFireball, IridiumIon Software
 */
class LuaHighlightingDefinition : HighlightingDefinition {

    override fun getLinePattern(): Pattern {
        return PATTERN_LINE
    }

    override fun getNumberPattern(): Pattern {
        return PATTERN_NUMBERS
    }

    override fun getPreprocessorPattern(): Pattern {
        return PATTERN_PREPROCESSOR
    }

    override fun getKeywordPattern(): Pattern {
        return PATTERN_KEYWORDS
    }

    override fun getBuiltinsPattern(): Pattern {
        return PATTERN_BUILTINS
    }

    override fun getCommentsPattern(): Pattern {
        return PATTERN_COMMENTS
    }

    override fun getStringPattern(): Pattern {
        return PATTERN_STRING
    }

    override fun getSymbolPattern(): Pattern {
        return PATTERN_SYMBOL
    }

    override fun getIdentifierPattern(): Pattern {
        return PATTERN_IDENTIFIER
    }

    companion object {
        //Default Highlighting definitions
        private val PATTERN_LINE = Pattern.compile(".*\\n")
        private val PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b")
        private val PATTERN_PREPROCESSOR = Pattern.compile(
                "$^")
        private val PATTERN_STRING = Pattern.compile("\"((\\\\[^\\n]|[^\"\\n])*)\"")
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(try|catch|finally|break|continue|" +
                        "for|do|end|repeat|until|continue|while|if|else|local|" +
                        "true|false|nil|return|" +
                        "public|static|final|private|protected|class|interface|import|package|this|super" +
                        ")\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(print|io|setmetatable|require|dofile|loadfile)\\b")
        private val PATTERN_COMMENTS = Pattern.compile("(?:--\\[(=*)\\[(.|\\n)*?\\]\\1\\])|(?:--.*)")
        private val PATTERN_SYMBOL = Pattern.compile("(\\{|\\}\\)|\\()")
        private val PATTERN_IDENTIFIER = Pattern.compile("((?<=function)\\s\\w*)") //match function names
    }
}