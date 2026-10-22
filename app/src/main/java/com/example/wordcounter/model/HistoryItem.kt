package com.example.wordcounter.model

import java.util.UUID

/**
 * Represents a single runtime calculation history record.
 *
 * @property id Unique identifier for list keys in UI.
 * @property text The exact text that was evaluated.
 * @property metrics The resulting metrics for this text.
 */
data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val metrics: TextMetrics
) {
    /**
     * Formats the record as: "text" => sentences, words, marks, numbers
     * e.g., "Labas rytas" => 1, 2, 11, 0
     */
    val formattedString: String
        get() = "\"$text\" => ${metrics.sentences}, ${metrics.words}, ${metrics.writingMarks}, ${metrics.numbers}"
}
