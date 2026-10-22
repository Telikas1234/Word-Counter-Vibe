package com.example.wordcounter.domain

import com.example.wordcounter.model.TextMetrics

/**
 * Pure Kotlin calculator for computing text metrics with zero UI dependencies.
 *
 * Metrics:
 * - sentences: Number of sentences (delimited by '.', '!', '?', or newlines;
 *   preserves decimal numbers such as "3.14" without splitting). Non-empty trailing text
 *   without terminating punctuation is counted as a sentence.
 * - words: Number of words containing alphabetic characters (pure numbers are excluded).
 * - writingMarks: Total character count including whitespace and punctuation marks.
 * - numbers: Number of standalone numeric tokens (integers and decimals).
 */
object TextMetricsCalculator {

    private val SURROUNDING_PUNCTUATION = charArrayOf(
        '(', ')', '[', ']', '{', '}', '<', '>',
        '"', '\'', '„', '“', '”', '«', '»',
        ',', ';', ':', '.', '!', '?',
        '%', '$', '€', '£'
    )

    private val NUMERIC_REGEX = Regex("""^[+-]?\d+([.,]\d+)?$""")
    private val DECIMAL_BETWEEN_DIGITS = Regex("""(?<=\d)\.(?=\d)""")
    private val SENTENCE_DELIMITERS = Regex("""[.!?\r\n]+""")

    /**
     * Calculates the four metrics for the given input text.
     */
    fun calculate(text: String): TextMetrics {
        if (text.isEmpty()) {
            return TextMetrics(sentences = 0, words = 0, writingMarks = 0, numbers = 0)
        }

        // 1. Writing marks: total number of characters
        val writingMarks = text.length

        // If only whitespace, return count with 0 words/numbers/sentences
        if (text.isBlank()) {
            return TextMetrics(sentences = 0, words = 0, writingMarks = writingMarks, numbers = 0)
        }

        // 2. Sentences calculation:
        // Protect decimal dots between digits with a private placeholder character
        val protectedText = DECIMAL_BETWEEN_DIGITS.replace(text, "\uFFFF")
        val sentenceSegments = protectedText.split(SENTENCE_DELIMITERS)
        val sentences = sentenceSegments.count { segment ->
            segment.any { it.isLetterOrDigit() }
        }

        // 3. Words and Numbers calculation:
        var words = 0
        var numbers = 0

        val tokens = text.trim().split(Regex("""\s+"""))
        for (rawToken in tokens) {
            val token = rawToken.trim(*SURROUNDING_PUNCTUATION)
            if (token.isEmpty()) continue

            if (token.matches(NUMERIC_REGEX)) {
                numbers++
            } else if (token.any { it.isLetter() }) {
                words++
            }
        }

        return TextMetrics(
            sentences = sentences,
            words = words,
            writingMarks = writingMarks,
            numbers = numbers
        )
    }
}
