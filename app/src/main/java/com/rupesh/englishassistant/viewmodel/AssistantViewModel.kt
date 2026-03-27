package com.rupesh.englishassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rupesh.englishassistant.model.HistoryEntry
import com.rupesh.englishassistant.model.TranslationResult
import com.rupesh.englishassistant.service.AppDatabase
import com.rupesh.englishassistant.service.LanguageDetector
import com.rupesh.englishassistant.service.TranslationEngine
import kotlinx.coroutines.launch

sealed class AssistantState {
    object Idle : AssistantState()
    object Listening : AssistantState()
    object Processing : AssistantState()
    data class ResultReady(val result: TranslationResult) : AssistantState()
    data class Error(val message: String) : AssistantState()
}

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val translationEngine = TranslationEngine()
    private val languageDetector = LanguageDetector()
    private val db = AppDatabase.getDatabase(application)
    private val historyDao = db.historyDao()

    private val _state = MutableLiveData<AssistantState>(AssistantState.Idle)
    val state: LiveData<AssistantState> = _state

    private val _currentResult = MutableLiveData<TranslationResult?>()
    val currentResult: LiveData<TranslationResult?> = _currentResult

    val history: LiveData<List<HistoryEntry>> = historyDao.getAllHistory()

    fun onSpeechReceived(spokenText: String) {
        viewModelScope.launch {
            _state.value = AssistantState.Processing
            try {
                val detectedLang = languageDetector.detectLanguage(spokenText)
                val result = translationEngine.translate(spokenText, detectedLang)
                _currentResult.value = result
                _state.value = AssistantState.ResultReady(result)

                // Save to history
                historyDao.insertEntry(
                    HistoryEntry(
                        originalText = spokenText,
                        language = detectedLang,
                        casualSuggestion = result.casualSuggestion,
                        professionalSuggestion = result.professionalSuggestion
                    )
                )
            } catch (e: Exception) {
                _state.value = AssistantState.Error("Translation failed: ${e.message}")
            }
        }
    }

    fun onListeningStarted() {
        _state.value = AssistantState.Listening
    }

    fun onError(message: String) {
        _state.value = AssistantState.Error(message)
    }

    fun resetState() {
        _state.value = AssistantState.Idle
        _currentResult.value = null
    }

    fun clearHistory() {
        viewModelScope.launch { historyDao.clearAll() }
    }

    override fun onCleared() {
        super.onCleared()
        languageDetector.close()
    }
}
