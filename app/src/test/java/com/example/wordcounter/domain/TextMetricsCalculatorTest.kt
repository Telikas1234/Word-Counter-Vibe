package com.example.wordcounter.domain

import com.example.wordcounter.model.TextMetrics
import org.junit.Assert.assertEquals
import org.junit.Test

class TextMetricsCalculatorTest {

    @Test
    fun testExample1_LabasRytas() {
        val input = "Labas rytas"
        val expected = TextMetrics(
            sentences = 1,
            words = 2,
            writingMarks = 11,
            numbers = 0
        )
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testExample2_Rugsejo1() {
        val input = "Rugsėjo 1 prasidėjo karas. Liūdna"
        val expected = TextMetrics(
            sentences = 2,
            words = 4,
            writingMarks = 33,
            numbers = 1
        )
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testEmptyInput() {
        val input = ""
        val expected = TextMetrics(
            sentences = 0,
            words = 0,
            writingMarks = 0,
            numbers = 0
        )
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testWhitespaceOnly() {
        val input = "     "
        val expected = TextMetrics(
            sentences = 0,
            words = 0,
            writingMarks = 5,
            numbers = 0
        )
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testDecimalsAndMultipleSentences() {
        val input = "Pelnas siekia 3.14 mln. eurų. Tai sudaro 15% prieaugį!"
        // Sentence 1: "Pelnas siekia 3.14 mln"
        // Sentence 2: " eurų"
        // Sentence 3: " Tai sudaro 15% prieaugį"
        // Words: Pelnas, siekia, mln, eurų, Tai, sudaro, prieaugį (7 words)
        // Numbers: 3.14, 15 (2 numbers)
        val metrics = TextMetricsCalculator.calculate(input)
        assertEquals(3, metrics.sentences)
        assertEquals(7, metrics.words)
        assertEquals(input.length, metrics.writingMarks)
        assertEquals(2, metrics.numbers)
    }

    @Test
    fun testMultiplePunctuationMarks() {
        val input = "Ar tikrai?!? Taip!"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(2, actual.sentences)
        assertEquals(3, actual.words)
        assertEquals(18, actual.writingMarks)
        assertEquals(0, actual.numbers)
    }
}
