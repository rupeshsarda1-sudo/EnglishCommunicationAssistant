package com.rupesh.englishassistant.service

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LanguageDetector {

    private val languageIdentifier = LanguageIdentification.getClient(
        LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.3f)
            .build()
    )

    suspend fun detectLanguage(text: String): String = suspendCancellableCoroutine { cont ->
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                val language = when (languageCode) {
                    "hi" -> "Hindi"
                    "mr" -> "Marathi"
                    "und" -> detectByRomanizedKeywords(text)
                    else -> detectByRomanizedKeywords(text)
                }
                cont.resume(language)
            }
            .addOnFailureListener {
                cont.resume(detectByRomanizedKeywords(text))
            }
    }

    // Fallback romanized keyword detection for Hinglish/Marathlish
    fun detectByRomanizedKeywords(text: String): String {
        val lower = text.lowercase()
        val words = lower.split(" ").toSet()

        val marathiWords = setOf("ahe", "nahi", "ka", "jhale", "ale", "mala", "tumhi",
            "ahet", "thamba", "pathavli", "tayar", "kiti", "lagel", "bolawat", "udya")
        val hindiWords = setOf("hai", "kya", "nahi", "karo", "ho", "gaya", "aaya",
            "raha", "mujhe", "tumne", "aata", "hun", "tha", "bhai", "yaar", "kal")

        val marathiScore = words.intersect(marathiWords).size
        val hindiScore = words.intersect(hindiWords).size

        return when {
            marathiScore > hindiScore -> "Marathi"
            hindiScore > 0 -> "Hindi"
            else -> "Hinglish/Mixed"
        }
    }

    fun close() {
        languageIdentifier.close()
    }
}
