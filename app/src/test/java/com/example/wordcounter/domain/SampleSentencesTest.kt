package com.example.wordcounter.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class SampleSentencesTest {

    @Test
    fun testSamplesListStructure() {
        val samples = SampleSentences.SAMPLES
        assertEquals(6, samples.size)
        for (sample in samples) {
            assertFalse("Sample should not be empty or blank", sample.isBlank())
        }
    }

    @Test
    fun testNextSampleReturnsValidSample() {
        val sample = SampleSentences.nextSample()
        assertTrue(
            "nextSample() must return an item present in SAMPLES",
            SampleSentences.SAMPLES.contains(sample)
        )
    }

    @Test
    fun testNextSampleCyclesSequentiallyAndWrapsAroundIndependentOfInitialState() {
        val samples = SampleSentences.SAMPLES
        val total = samples.size

        // Capture whatever sample is next, independent of previous test execution order
        val initialSample = SampleSentences.nextSample()
        val startIndex = samples.indexOf(initialSample)
        assertTrue("Initial sample must be present in SAMPLES", startIndex >= 0)

        // Verify that the next 2 full cycles (2 * size) follow the exact sequential order with wrap-around
        for (step in 1..(total * 2)) {
            val expectedIndex = (startIndex + step) % total
            val expectedSample = samples[expectedIndex]
            val actualSample = SampleSentences.nextSample()
            assertEquals("Sample at cycle step $step should match expected sequential item", expectedSample, actualSample)
        }
    }

    @Test
    fun testNextSampleThreadSafety() {
        val executor = Executors.newFixedThreadPool(4)
        val numCalls = 50
        val tasks = (1..numCalls).map {
            Callable { SampleSentences.nextSample() }
        }

        val results = executor.invokeAll(tasks)
        executor.shutdown()

        assertEquals(numCalls, results.size)
        for (future in results) {
            val sample = future.get()
            assertTrue(
                "Concurrently fetched sample must be one of the known samples",
                SampleSentences.SAMPLES.contains(sample)
            )
        }
    }
}
