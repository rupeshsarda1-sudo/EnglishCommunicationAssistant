package com.rupesh.englishassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rupesh.englishassistant.model.HistoryEntry
import com.rupesh.englishassistant.model.TranslationResult
import com.rupesh.englishassistant.service.AppDatabase
import com.rupesh.englishassistant.service.TranslationEngine
import kotlinx.coroutines.launch

sealed class AssistantState {
    object Idle : AssistantState()
    object Listening : AssistantState()
    object Processing : AssistantState()
    data class Result(val result: TranslationResult) : AssistantState()
    data class Error(val message: String) : AssistantState()
}

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val translationEngine = TranslationEngine()
    private val db = AppDatabase.getDatabase(application)
    private val historyDao = db.historyDao()

    private val _state = MutableLiveData<AssistantState>(AssistantState.Idle)
    val state: LiveData<AssistantState> = _state

    private val _currentResult = MutableLiveData<TranslationResult?>()
    val currentResult: LiveData<TranslationResult?> = _currentResult

    val history: LiveData<List<HistoryEntry>> = historyDao.getAllHistory()

    fun processInput(text: String) {
        if (text.isBlank()) {
            _state.value = AssistantState.Error("Please speak or type something")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = AssistantState.Processing
                val result = translationEngine.translate(text)
                _currentResult.value = result
                _state.value = AssistantState.Result(result)

                // Save to history
                historyDao.insertEntry(
                    HistoryEntry(
                        originalText = text,
                        language = result.detectedLanguage,
                        casualSuggestion = result.casualEnglish,
                        professionalSuggestion = result.professionalEnglish
                    )
                )
            } catch (e: Exception) {
                _state.value = AssistantState.Error("Translation failed: ${e.message}")
            }
        }
    }

    fun setListening() {
        _state.value = AssistantState.Listening
    }

    fun setError(message: String) {
        _state.value = AssistantState.Error(message)
    }

    fun reset() {
        _state.value = AssistantState.Idle
        _currentResult.value = null
    }

    fun clearHistory() {
        viewModelScope.launch { historyDao.clearAll() }
    }
}
