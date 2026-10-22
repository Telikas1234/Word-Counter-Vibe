package com.example.wordcounter.model

/**
 * Data class holding the four calculated text metrics.
 *
 * @property sentences Number of sentences.
 * @property words Number of words (tokens with alphabetic letters, excluding pure numbers).
 * @property writingMarks Total character count including spaces and punctuation.
 * @property numbers Number of standalone numeric tokens.
 */
data class TextMetrics(
    val sentences: Int,
    val words: Int,
    val writingMarks: Int,
    val numbers: Int
)
