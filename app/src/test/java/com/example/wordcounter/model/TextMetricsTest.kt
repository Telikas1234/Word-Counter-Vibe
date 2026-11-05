package com.example.wordcounter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextMetricsTest {

    @Test
    fun testConstructorAndGetters() {
        val metrics = TextMetrics(
            sentences = 3,
            words = 15,
            writingMarks = 95,
            numbers = 2
        )
        assertEquals(3, metrics.sentences)
        assertEquals(15, metrics.words)
        assertEquals(95, metrics.writingMarks)
        assertEquals(2, metrics.numbers)
    }

    @Test
    fun testComponentDestructuring() {
        val metrics = TextMetrics(1, 2, 3, 4)
        val (sentences, words, writingMarks, numbers) = metrics
        assertEquals(1, sentences)
        assertEquals(2, words)
        assertEquals(3, writingMarks)
        assertEquals(4, numbers)
    }

    @Test
    fun testCopy() {
        val original = TextMetrics(1, 2, 3, 4)
        val copyNoArgs = original.copy()
        assertEquals(original, copyNoArgs)

        val copySentences = original.copy(sentences = 10)
        assertEquals(10, copySentences.sentences)
        assertEquals(2, copySentences.words)

        val copyWords = original.copy(words = 20)
        assertEquals(20, copyWords.words)

        val copyMarks = original.copy(writingMarks = 30)
        assertEquals(30, copyMarks.writingMarks)

        val copyNumbers = original.copy(numbers = 40)
        assertEquals(40, copyNumbers.numbers)
    }

    @Test
    fun testEqualsAndHashCode() {
        val m1 = TextMetrics(1, 2, 3, 4)
        val m2 = TextMetrics(1, 2, 3, 4)
        val diffSentences = TextMetrics(9, 2, 3, 4)
        val diffWords = TextMetrics(1, 9, 3, 4)
        val diffMarks = TextMetrics(1, 2, 9, 4)
        val diffNumbers = TextMetrics(1, 2, 3, 9)

        // Same reference
        assertTrue(m1.equals(m1))
        // Equal values
        assertTrue(m1 == m2)
        assertEquals(m1.hashCode(), m2.hashCode())

        // Non-null vs null
        assertFalse(m1.equals(null))
        // Different type
        assertFalse(m1.equals("Some String"))

        // Differing fields
        assertFalse(m1 == diffSentences)
        assertFalse(m1 == diffWords)
        assertFalse(m1 == diffMarks)
        assertFalse(m1 == diffNumbers)

        assertNotEquals(m1.hashCode(), diffSentences.hashCode())
    }

    @Test
    fun testToString() {
        val metrics = TextMetrics(1, 2, 3, 4)
        val str = metrics.toString()
        assertTrue(str.contains("sentences=1"))
        assertTrue(str.contains("words=2"))
        assertTrue(str.contains("writingMarks=3"))
        assertTrue(str.contains("numbers=4"))
    }
}
