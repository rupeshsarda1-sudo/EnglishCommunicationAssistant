package com.rupesh.englishassistant.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

data class TranslationResult(
    val detectedInput: String = "",
    val detectedLanguage: String = "",
    val meaning: String = "",
    val casualSuggestion: String = "",
    val professionalSuggestion: String = "",
    val alternativeSuggestion: String = "",
    val context: ConversationContext = ConversationContext.CASUAL,
    val confidence: Float = 0f
)

enum class ConversationContext {
    CASUAL, WORKPLACE, FORMAL
}

@Entity(tableName = "history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val language: String,
    val casualSuggestion: String,
    val professionalSuggestion: String,
    val timestamp: Long = System.currentTimeMillis()
)
