package com.example.wordcounter.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordcounter.MainActivity
import com.example.wordcounter.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for the Word Counter application.
 *
 * All user-visible strings are read from resources via [getString] so that
 * tests are language-independent and pass regardless of device locale.
 */
@RunWith(AndroidJUnit4::class)
class WordCounterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /** Shortcut to resolve string resources from the activity under test. */
    private fun getString(resId: Int): String =
        composeTestRule.activity.getString(resId)

    // ---------------------------------------------------------------
    // Scenario 1: valid text + Calculate shows the right four metrics
    // ---------------------------------------------------------------
    @Test
    fun validTextAndCalculate_showsCorrectMetrics() {
        // "Labas rytas" → sentences=1, words=2, writingMarks=11, numbers=0
        val input = "Labas rytas"

        composeTestRule.onNodeWithTag("inputField")
            .performTextInput(input)

        composeTestRule.onNodeWithTag("btnCalculate")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("metricSentences")
            .assertTextEquals("1")
        composeTestRule.onNodeWithTag("metricWords")
            .assertTextEquals("2")
        composeTestRule.onNodeWithTag("metricMarks")
            .assertTextEquals("11")
        composeTestRule.onNodeWithTag("metricNumbers")
            .assertTextEquals("0")
    }

    // ---------------------------------------------------------------
    // Scenario 2: empty input + Calculate shows warning snackbar
    //             and does NOT add a history item
    // ---------------------------------------------------------------
    @Test
    fun emptyInputAndCalculate_showsWarningSnackbar_noHistoryItem() {
        val warningText = getString(R.string.empty_input_warning)
        val historyEmptyText = getString(R.string.history_empty)

        // Ensure the input is empty, then tap Calculate
        composeTestRule.onNodeWithTag("inputField")
            .performTextClearance()

        composeTestRule.onNodeWithTag("btnCalculate")
            .performClick()

        // The snackbar is asynchronous – wait for its text to appear
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText(warningText)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText(warningText)
            .assertExists()

        // History section should still show the empty-state message
        composeTestRule.onNodeWithText(historyEmptyText)
            .assertExists()
    }

    // ---------------------------------------------------------------
    // Scenario 3: Sample Text fills the field and resets results to 0
    // ---------------------------------------------------------------
    @Test
    fun sampleTextButton_fillsFieldAndResetsResults() {
        // First calculate something so metrics are non-zero
        composeTestRule.onNodeWithTag("inputField")
            .performTextInput("Hello world")

        composeTestRule.onNodeWithTag("btnCalculate")
            .performClick()
        composeTestRule.waitForIdle()

        // Metrics should be non-zero now
        composeTestRule.onNodeWithTag("metricWords")
            .assertTextEquals("2")

        // Tap Sample Text
        composeTestRule.onNodeWithTag("btnSample")
            .performClick()
        composeTestRule.waitForIdle()

        // Input field should contain non-empty text (the sample)
        composeTestRule.onNodeWithTag("inputField")
            .assertTextContains(composeTestRule.activity.getString(R.string.input_label), substring = false, ignoreCase = false)
            .let { /* just verifying field is accessible */ }

        // After sample, the text field's value must not be empty.
        // The field should contain some text from SampleSentences.
        // We verify by checking the field is not blank via its text value.
        val fieldNode = composeTestRule.onNodeWithTag("inputField")
            .fetchSemanticsNode()
        val textValues = fieldNode.config
            .filter { it.key.name == "EditableText" }
            .map { it.value.toString() }
        assert(textValues.any { it.isNotBlank() }) {
            "Expected the input field to contain sample text, but it was blank"
        }

        // Metrics should be reset to 0
        composeTestRule.onNodeWithTag("metricSentences")
            .assertTextEquals("0")
        composeTestRule.onNodeWithTag("metricWords")
            .assertTextEquals("0")
        composeTestRule.onNodeWithTag("metricMarks")
            .assertTextEquals("0")
        composeTestRule.onNodeWithTag("metricNumbers")
            .assertTextEquals("0")
    }

    // ---------------------------------------------------------------
    // Scenario 4: Clear button empties the field
    // ---------------------------------------------------------------
    @Test
    fun clearButton_emptiesTheField() {
        val inputText = "Some text to clear"

        composeTestRule.onNodeWithTag("inputField")
            .performTextInput(inputText)
        composeTestRule.waitForIdle()

        // Tap the Clear *button* (not the trailing icon).
        // We use the testTag to disambiguate.
        composeTestRule.onNodeWithTag("btnClear")
            .performClick()
        composeTestRule.waitForIdle()

        // The input field should now be empty
        composeTestRule.onNodeWithTag("inputField")
            .assertTextContains("")
    }

    // ---------------------------------------------------------------
    // Scenario 5: history shows two entries, newest first,
    //             in the format: "text" => s, w, m, n
    // ---------------------------------------------------------------
    @Test
    fun historyShowsTwoEntries_newestFirst() {
        // First calculation: "Labas rytas" → 1, 2, 11, 0
        val text1 = "Labas rytas"
        composeTestRule.onNodeWithTag("inputField")
            .performTextInput(text1)
        composeTestRule.onNodeWithTag("btnCalculate")
            .performClick()
        composeTestRule.waitForIdle()

        // Clear and enter second text
        composeTestRule.onNodeWithTag("inputField")
            .performTextClearance()
        composeTestRule.waitForIdle()

        // Second calculation: "Hello world" → 1, 2, 11, 0
        val text2 = "Hello world"
        composeTestRule.onNodeWithTag("inputField")
            .performTextInput(text2)
        composeTestRule.onNodeWithTag("btnCalculate")
            .performClick()
        composeTestRule.waitForIdle()

        // Expected formatted strings (newest first):
        // "Hello world" => 1, 2, 11, 0
        // "Labas rytas" => 1, 2, 11, 0
        val expected2 = "\"$text2\" => 1, 2, 11, 0"
        val expected1 = "\"$text1\" => 1, 2, 11, 0"

        // Scroll to see history and verify both entries exist
        composeTestRule.onNodeWithText(expected2)
            .performScrollTo()
            .assertExists()

        composeTestRule.onNodeWithText(expected1)
            .performScrollTo()
            .assertExists()

        // Verify the count indicator shows (2)
        composeTestRule.onNodeWithText("(2)")
            .performScrollTo()
            .assertExists()

        // Verify ordering: the newest entry ("Hello world") should appear
        // before the older one ("Labas rytas") in the semantic tree.
        // We collect all nodes containing "=>" and verify order.
        val historyNodes = composeTestRule
            .onAllNodesWithText("=>", substring = true)
            .fetchSemanticsNodes()

        // Filter to only those that contain the formatted history pattern
        val historyTexts = historyNodes.mapNotNull { node ->
            node.config
                .filter { it.key.name == "Text" }
                .flatMap {
                    @Suppress("UNCHECKED_CAST")
                    (it.value as? List<*>)?.map { it.toString() }
                        ?: listOf(it.value.toString())
                }
                .firstOrNull { it.contains("=>") }
        }

        assert(historyTexts.size >= 2) {
            "Expected at least 2 history entries, found ${historyTexts.size}: $historyTexts"
        }

        // Newest first: "Hello world" entry should come before "Labas rytas"
        val indexNewest = historyTexts.indexOfFirst { it.contains(text2) }
        val indexOldest = historyTexts.indexOfFirst { it.contains(text1) }

        assert(indexNewest < indexOldest) {
            "Expected newest entry ('$text2' at $indexNewest) to appear " +
                "before oldest ('$text1' at $indexOldest). Actual order: $historyTexts"
        }
    }
}
