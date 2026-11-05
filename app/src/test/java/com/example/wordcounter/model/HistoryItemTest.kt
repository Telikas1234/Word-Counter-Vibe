package com.example.wordcounter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryItemTest {

    @Test
    fun testDefaultIdConstructorGeneratesUniqueUUID() {
        val metrics = TextMetrics(1, 2, 11, 0)
        val item1 = HistoryItem(text = "First", metrics = metrics)
        val item2 = HistoryItem(text = "Second", metrics = metrics)

        assertNotNull(item1.id)
        assertTrue(item1.id.isNotEmpty())
        assertNotNull(item2.id)
        assertTrue(item2.id.isNotEmpty())
        assertNotEquals(item1.id, item2.id)
    }

    @Test
    fun testExplicitConstructorAndGetters() {
        val metrics = TextMetrics(2, 4, 33, 1)
        val item = HistoryItem(
            id = "custom-id-42",
            text = "Rugsėjo 1 prasidėjo karas. Liūdna",
            metrics = metrics
        )

        assertEquals("custom-id-42", item.id)
        assertEquals("Rugsėjo 1 prasidėjo karas. Liūdna", item.text)
        assertEquals(metrics, item.metrics)
    }

    @Test
    fun testFormattedString() {
        val metrics = TextMetrics(1, 2, 11, 0)
        val item = HistoryItem(
            id = "id-1",
            text = "Labas rytas",
            metrics = metrics
        )

        val expected = "\"Labas rytas\" => 1, 2, 11, 0"
        assertEquals(expected, item.formattedString)
    }

    @Test
    fun testComponentDestructuring() {
        val metrics = TextMetrics(1, 2, 3, 4)
        val item = HistoryItem(id = "comp-id", text = "Text", metrics = metrics)

        val (id, text, itemMetrics) = item
        assertEquals("comp-id", id)
        assertEquals("Text", text)
        assertEquals(metrics, itemMetrics)
    }

    @Test
    fun testCopy() {
        val originalMetrics = TextMetrics(1, 2, 10, 0)
        val original = HistoryItem(id = "item-id", text = "Original text", metrics = originalMetrics)

        val copyNoArgs = original.copy()
        assertEquals(original, copyNoArgs)

        val copyId = original.copy(id = "new-id")
        assertEquals("new-id", copyId.id)
        assertEquals(original.text, copyId.text)

        val copyText = original.copy(text = "Modified text")
        assertEquals("Modified text", copyText.text)
        assertEquals(original.id, copyText.id)

        val newMetrics = TextMetrics(3, 5, 20, 1)
        val copyMetrics = original.copy(metrics = newMetrics)
        assertEquals(newMetrics, copyMetrics.metrics)
    }

    @Test
    fun testEqualsAndHashCode() {
        val m = TextMetrics(1, 2, 3, 4)
        val item1 = HistoryItem("id-1", "Hello", m)
        val item2 = HistoryItem("id-1", "Hello", m)
        val diffId = HistoryItem("id-2", "Hello", m)
        val diffText = HistoryItem("id-1", "Different", m)
        val diffMetrics = HistoryItem("id-1", "Hello", TextMetrics(0, 0, 0, 0))

        // Same reference
        assertTrue(item1.equals(item1))
        // Equal values
        assertTrue(item1 == item2)
        assertEquals(item1.hashCode(), item2.hashCode())

        // Non-null vs null
        assertFalse(item1.equals(null))
        // Different type
        assertFalse(item1.equals("Not a HistoryItem"))

        // Differing fields
        assertFalse(item1 == diffId)
        assertFalse(item1 == diffText)
        assertFalse(item1 == diffMetrics)

        assertNotEquals(item1.hashCode(), diffId.hashCode())
    }

    @Test
    fun testToString() {
        val item = HistoryItem("id-test", "Hello", TextMetrics(1, 1, 5, 0))
        val str = item.toString()
        assertTrue(str.contains("id-test"))
        assertTrue(str.contains("Hello"))
    }
}
