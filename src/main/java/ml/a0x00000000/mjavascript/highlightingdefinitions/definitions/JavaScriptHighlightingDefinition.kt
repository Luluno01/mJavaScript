package ml.a0x00000000.mjavascript.highlightingdefinitions.definitions

import java.util.regex.Pattern

import ml.a0x00000000.mjavascript.editor.HighlightingDefinition

/**
 * Author: 0xFireball
 */
class JavaScriptHighlightingDefinition : HighlightingDefinition {


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
                "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" + "#error|#pragma|#extension|#version|#line)\\b",
                Pattern.MULTILINE)
        private val PATTERN_STRING = Pattern.compile("\"((\\\\[^\\n]|[^\"\\n])*)\"")
        private val PATTERN_KEYWORDS = Pattern.compile(
                "\\b(let|var|try|catch|break|continue|" + "do|for|while|if|else|switch|in|out|inout|float|int|void|bool|true|false|new|function)\\b")
        private val PATTERN_BUILTINS = Pattern.compile(
                "\\b(radians|degrees|sin|cos|tan|asin|acos|atan|pow|JSON|document|window|location|console)\\b")
        private val PATTERN_COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
        private val PATTERN_SYMBOL = Pattern.compile("(\\{|\\}\\)|\\()")
        private val PATTERN_IDENTIFIER = Pattern.compile("a^")
    }
}
