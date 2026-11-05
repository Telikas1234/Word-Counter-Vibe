package com.example.wordcounter.domain

import com.example.wordcounter.model.TextMetrics
import org.junit.Assert.assertEquals
import org.junit.Test

class TextMetricsCalculatorTest {

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
        val input = "   \t\r\n   "
        val expected = TextMetrics(
            sentences = 0,
            words = 0,
            writingMarks = input.length,
            numbers = 0
        )
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(expected, actual)
    }

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
    fun testDecimalsAndMultipleSentences() {
        val input = "Pelnas siekia 3.14 mln. eurų. Tai sudaro 15% prieaugį!"
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

    @Test
    fun testLithuanianCharacters() {
        // Contains all Lithuanian specific letters: ą, č, ę, ė, į, š, ų, ū, ž (lower and upper)
        val input = "Ąžuolas čia auga. Įlanka plati, ėriukas bėga. Žąsis su šunimi ir ūsuotu kačiuku!"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(3, actual.sentences)
        assertEquals(13, actual.words)
        assertEquals(input.length, actual.writingMarks)
        assertEquals(0, actual.numbers)
    }

    @Test
    fun testNumericVariations() {
        // Positive, negative, integer, decimal with dot and comma
        val input = "42 +100 -5 3.14 -0.5 +2.718 3,14 -0,5 +1,414"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(1, actual.sentences)
        assertEquals(0, actual.words)
        assertEquals(input.length, actual.writingMarks)
        assertEquals(9, actual.numbers)
    }

    @Test
    fun testSurroundingPunctuationVarieties() {
        // Test various surrounding punctuation marks: (), [], {}, <>, quotes, currency symbols
        val input = "„Žodis“ «kitas» (trečias) [ketvirtas] {penktas} <šeštas> 'septintas' \"aštuntas\" $100 50€ 20£"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(1, actual.sentences)
        assertEquals(8, actual.words)
        assertEquals(input.length, actual.writingMarks)
        assertEquals(3, actual.numbers)
    }

    @Test
    fun testPunctuationOnlyTokens_trimmedToEmpty() {
        // Tokens that consist entirely of surrounding punctuation should be ignored (token.isEmpty() == true)
        val input = "Labas ... rytas € ? irgi"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(3, actual.sentences) // "Labas ", " rytas € ", " irgi"
        assertEquals(3, actual.words) // Labas, rytas, irgi
        assertEquals(input.length, actual.writingMarks)
        assertEquals(0, actual.numbers)

        // Single sentence with surrounding punctuation tokens
        val inputSingle = "Labas ( ) [ ] rytas € £ $"
        val actualSingle = TextMetricsCalculator.calculate(inputSingle)
        assertEquals(1, actualSingle.sentences)
        assertEquals(2, actualSingle.words) // Labas, rytas
        assertEquals(inputSingle.length, actualSingle.writingMarks)
        assertEquals(0, actualSingle.numbers)
    }

    @Test
    fun testNonWordNonNumberSymbols_fallThroughBothBranches() {
        // Tokens like "***" or "###" contain no letters and are not numeric regex matches
        val input = "Sveiki *** ### @@@ pasauli"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(1, actual.sentences)
        assertEquals(2, actual.words) // Sveiki, pasauli
        assertEquals(input.length, actual.writingMarks)
        assertEquals(0, actual.numbers)
    }

    @Test
    fun testSentenceDelimitersWithNewlinesAndNonLetterSegments() {
        // Newlines, carriage returns, and segments that contain only punctuation (no letters or digits)
        val input = "Pirmas sakinys.\n\r... \r\nAntras sakinys!"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(2, actual.sentences)
        assertEquals(4, actual.words)
        assertEquals(input.length, actual.writingMarks)
        assertEquals(0, actual.numbers)
    }

    @Test
    fun testDelimitersOnly_noLetterOrDigit() {
        val input = "... !?! \r\n --- ???"
        val actual = TextMetricsCalculator.calculate(input)
        assertEquals(0, actual.sentences)
        assertEquals(0, actual.words)
        assertEquals(input.length, actual.writingMarks)
        assertEquals(0, actual.numbers)
    }
}
