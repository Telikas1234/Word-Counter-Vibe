package com.example.wordcounter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordcounter.R
import com.example.wordcounter.domain.SampleSentences
import com.example.wordcounter.domain.TextMetricsCalculator
import com.example.wordcounter.model.HistoryItem
import com.example.wordcounter.model.TextMetrics
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing UI state and user interactions for Word Counter.
 */
class WordCounterViewModel : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _currentMetrics = MutableStateFlow<TextMetrics?>(null)
    val currentMetrics: StateFlow<TextMetrics?> = _currentMetrics.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _warningEvent = MutableSharedFlow<Int>()
    val warningEvent: SharedFlow<Int> = _warningEvent.asSharedFlow()

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
        _currentMetrics.value = null
    }

    /**
     * Triggers metric calculation.
     * If the input field is empty or contains only whitespace, triggers an alert warning
     * instead of calculating.
     */
    fun onCalculateClicked() {
        val current = _inputText.value
        if (current.isBlank()) {
            viewModelScope.launch {
                _warningEvent.emit(R.string.empty_input_warning)
            }
            return
        }

        val metrics = TextMetricsCalculator.calculate(current)
        _currentMetrics.value = metrics

        val item = HistoryItem(
            text = current,
            metrics = metrics
        )
        // Prepend to runtime history list
        _history.value = listOf(item) + _history.value
    }

    /**
     * Fills the input field with the next sample sentence and clears stale metrics.
     */
    fun onSampleClicked() {
        val sample = SampleSentences.nextSample()
        _inputText.value = sample
        _currentMetrics.value = null
    }
    /**
     * Clears the current input and active metrics.
     */
    fun onClearClicked() {
        _inputText.value = ""
        _currentMetrics.value = null
    }
}