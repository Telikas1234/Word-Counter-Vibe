package com.example.wordcounter.ui

import com.example.wordcounter.R
import com.example.wordcounter.domain.SampleSentences
import com.example.wordcounter.model.TextMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WordCounterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val viewModel = WordCounterViewModel()
        assertEquals("", viewModel.inputText.value)
        assertNull(viewModel.currentMetrics.value)
        assertTrue(viewModel.history.value.isEmpty())
    }

    @Test
    fun testEmptyInput_triggersWarningEventAndDoesNotCalculate() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val warnings = mutableListOf<Int>()

        // warningEvent is a SharedFlow without replay, so start collecting BEFORE triggering the action
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.warningEvent.collect { warnings.add(it) }
        }

        viewModel.onInputTextChanged("")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        assertEquals(listOf(R.string.empty_input_warning), warnings)
        assertNull(viewModel.currentMetrics.value)
        assertTrue(viewModel.history.value.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun testBlankWhitespaceInput_triggersWarningEventAndDoesNotCalculate() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val warnings = mutableListOf<Int>()

        // Start collecting before triggering the action
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.warningEvent.collect { warnings.add(it) }
        }

        viewModel.onInputTextChanged("   \t\r\n   ")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        assertEquals(listOf(R.string.empty_input_warning), warnings)
        assertNull(viewModel.currentMetrics.value)
        assertTrue(viewModel.history.value.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun testCalculateClickedWithValidInput_calculatesMetricsAndUpdatesHistory() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val warnings = mutableListOf<Int>()

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.warningEvent.collect { warnings.add(it) }
        }

        viewModel.onInputTextChanged("Labas rytas")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        // No warnings emitted
        assertTrue(warnings.isEmpty())

        val expectedMetrics = TextMetrics(sentences = 1, words = 2, writingMarks = 11, numbers = 0)
        assertEquals(expectedMetrics, viewModel.currentMetrics.value)

        // History contains 1 item
        assertEquals(1, viewModel.history.value.size)
        val historyItem = viewModel.history.value[0]
        assertEquals("Labas rytas", historyItem.text)
        assertEquals(expectedMetrics, historyItem.metrics)

        collectJob.cancel()
    }

    @Test
    fun testCalculateWithDecimals() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val input = "Kaina yra 3.14 euro už 2.5 kg."
        viewModel.onInputTextChanged(input)
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        val metrics = viewModel.currentMetrics.value
        assertNotNull(metrics)
        assertEquals(1, metrics?.sentences)
        assertEquals(5, metrics?.words)
        assertEquals(input.length, metrics?.writingMarks)
        assertEquals(2, metrics?.numbers)
    }

    @Test
    fun testCalculateWithMultiplePunctuation() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val input = "Ar tikrai?!? Tikrai!"
        viewModel.onInputTextChanged(input)
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        val metrics = viewModel.currentMetrics.value
        assertNotNull(metrics)
        assertEquals(2, metrics?.sentences)
        assertEquals(3, metrics?.words)
        assertEquals(input.length, metrics?.writingMarks)
        assertEquals(0, metrics?.numbers)
    }

    @Test
    fun testCalculateWithLithuanianCharacters() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        val input = "Ąžuolas, šaka, įlanka, ūkas, žąsis, čiaupas, ėriukas, tęsinys."
        viewModel.onInputTextChanged(input)
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        val metrics = viewModel.currentMetrics.value
        assertNotNull(metrics)
        assertEquals(1, metrics?.sentences)
        assertEquals(8, metrics?.words)
        assertEquals(input.length, metrics?.writingMarks)
        assertEquals(0, metrics?.numbers)
    }

    @Test
    fun testHistoryOrdering_prependsNewestItems() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()

        viewModel.onInputTextChanged("Pirmas sakinys")
        viewModel.onCalculateClicked()

        viewModel.onInputTextChanged("Antras sakinys")
        viewModel.onCalculateClicked()

        viewModel.onInputTextChanged("Trečias sakinys")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        val history = viewModel.history.value
        assertEquals(3, history.size)
        // Verify newest is first (index 0), followed by previous ones
        assertEquals("Trečias sakinys", history[0].text)
        assertEquals("Antras sakinys", history[1].text)
        assertEquals("Pirmas sakinys", history[2].text)
    }

    @Test
    fun testStaleMetricsResetOnInputTextChanged() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        viewModel.onInputTextChanged("Tekstas")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        assertNotNull(viewModel.currentMetrics.value)

        // Modifying text must reset currentMetrics to null
        viewModel.onInputTextChanged("Naujas tekstas")
        assertNull(viewModel.currentMetrics.value)
        assertEquals("Naujas tekstas", viewModel.inputText.value)
    }

    @Test
    fun testStaleMetricsResetAndInputUpdateOnSampleClicked() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        viewModel.onInputTextChanged("Tekstas")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        assertNotNull(viewModel.currentMetrics.value)

        // Calling onSampleClicked sets next sample and resets currentMetrics to null
        viewModel.onSampleClicked()
        assertNull(viewModel.currentMetrics.value)
        assertTrue(
            "Input text should be one of the known sample sentences",
            SampleSentences.SAMPLES.contains(viewModel.inputText.value)
        )
    }

    @Test
    fun testClearClickedResetsInputAndMetrics() = runTest(testDispatcher) {
        val viewModel = WordCounterViewModel()
        viewModel.onInputTextChanged("Valomas tekstas")
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        assertNotNull(viewModel.currentMetrics.value)
        assertEquals("Valomas tekstas", viewModel.inputText.value)
        assertEquals(1, viewModel.history.value.size)

        // Clearing resets input and metrics, while retaining history
        viewModel.onClearClicked()
        assertEquals("", viewModel.inputText.value)
        assertNull(viewModel.currentMetrics.value)
        assertEquals(1, viewModel.history.value.size)
    }
}
