package ml.a0x00000000.mjavascript.highlightingdefinitions.definitions

import ml.a0x00000000.mjavascript.editor.HighlightingDefinition
import java.util.regex.Pattern

/**
 * Author: 0xFireball, IridiumIon Software
 */
class CPlusPlusHighlightingDefinition : HighlightingDefinition {

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
                "^[\t ]*(#include|#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" + "#error|#pragma|#extension|#version|#line)\\b",
                Pattern.MULTILINE)
        private val PATTERN_STRING = Pattern.compile("\"((\\\\[^\\n]|[^\"\\n])*)\"")
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(var|try|catch|finally|break|continue|" +
                        "do|for|foreach|continue|while|if|else|switch|in|is|as|float|int|void|bool|true|false|new|" +
                        "public|static|readonly|const|private|protected|class|interface|using|namespace|struct|this|base|" +
                        "true|false|null|return|" +
                        "virtual|internal|abstract|override|async|await|explicit|ref|out|extern|checked|unchecked|" +
                        "continue|enum|lock|partial|params|typeof|unsafe|implicit|default|let|yield|value|operator|global" +
                        ")\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(void|int|long|ulong|float|double|bool|short|byte|char|object|string|dynamic|" +
                        "cout|cin|endl|string" +
                        ")\\b")
        private val PATTERN_COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
        private val PATTERN_SYMBOL = Pattern.compile("(\\{|\\}\\)|\\()")
        private val PATTERN_IDENTIFIER = Pattern.compile("((?<=class)\\s\\w*)|" +
                "((?<=struct)\\s\\w*)|" +
                "((?<=typedef)\\s\\w*)|" +
                "((?<=using)\\s(\\w|\\.)+[^;])|" + //Match everything between import and semicolon

                "((?<=namespace)\\s(\\w|\\.)+[^;])")
    }
}