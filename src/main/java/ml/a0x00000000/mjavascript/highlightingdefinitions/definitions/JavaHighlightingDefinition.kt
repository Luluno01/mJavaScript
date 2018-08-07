package ml.a0x00000000.mjavascript.highlightingdefinitions.definitions

import java.util.regex.Pattern

import ml.a0x00000000.mjavascript.editor.HighlightingDefinition

/**
 * Author: 0xFireball, IridiumIon Software
 */
class JavaHighlightingDefinition : HighlightingDefinition {

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
        private val PATTERN_LINE = Pattern.compile(".*\\n")
        private val PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b")
        private val PATTERN_PREPROCESSOR = Pattern.compile(
                "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" + "#error|#pragma|#extension|#version|#line)\\b",
                Pattern.MULTILINE)
        private val PATTERN_STRING = Pattern.compile("\"((\\\\[^\\n]|[^\"\\n])*)\"")
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(var|try|catch|finally|break|continue|" +
                        "do|for|continue|while|if|else|switch|in|instanceof|float|int|void|bool|true|false|new|" +
                        "true|false|null|return|" +
                        "public|static|final|private|protected|class|interface|import|package|this|super" +
                        ")\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(void|int|long|float|double|boolean|char|Object|String)\\b")
        private val PATTERN_COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
        private val PATTERN_SYMBOL = Pattern.compile("(\\{|\\}\\)|\\()") //TODO: Are we sure about this?
        private val PATTERN_IDENTIFIER = Pattern.compile("((?<=class)\\s\\w*)|" +
                "((?<=import)\\s(\\w|\\.)+[^;])|" + //Match everything between import and semicolon

                "((?<=package)\\s(\\w|\\.)+[^;])")
    }
}