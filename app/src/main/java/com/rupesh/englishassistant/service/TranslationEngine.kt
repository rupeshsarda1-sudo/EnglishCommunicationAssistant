package com.rupesh.englishassistant.service

import com.rupesh.englishassistant.model.ConversationContext
import com.rupesh.englishassistant.model.TranslationResult

data class PhraseEntry(
    val meaning: String = "",
    val casual: String,
    val professional: String,
    val alternative: String = ""
)

class TranslationEngine {

    private val workplaceKeywords = setOf(
        "meeting", "boss", "client", "file", "report", "deadline", "office",
        "project", "email", "presentation", "review", "task", "kaam", "manager",
        "salary", "leave", "hr", "interview", "promotion", "appraisal", "resign"
    )

    // ─────────────────────── Hindi Dictionary ───────────────────────
    private val hindiDictionary = mapOf(
        "kal meeting hai kya confirm nahi hai" to PhraseEntry(
            meaning = "Not sure if meeting is tomorrow",
            casual = "Is tomorrow's meeting confirmed?",
            professional = "Could you please confirm if the meeting is scheduled for tomorrow?",
            alternative = "Has the meeting for tomorrow been confirmed?"
        ),
        "tumne file bheja kya" to PhraseEntry(
            meaning = "Did you send the file",
            casual = "Did you send the file?",
            professional = "Have you shared the file?",
            alternative = "Was the file sent from your end?"
        ),
        "boss ne bulaya hai" to PhraseEntry(
            casual = "The boss wants to see you.",
            professional = "The manager has requested your presence.",
            alternative = "You've been called by the manager."
        ),
        "kaam ho gaya kya" to PhraseEntry(
            casual = "Is the work done?",
            professional = "Has the task been completed?",
            alternative = "Can you confirm if the work is finished?"
        ),
        "thodi der mein aata hun" to PhraseEntry(
            casual = "I'll be there in a bit.",
            professional = "I will join shortly.",
            alternative = "I'll be with you in a moment."
        ),
        "report ready hai kya" to PhraseEntry(
            casual = "Is the report ready?",
            professional = "Has the report been prepared?",
            alternative = "Can you confirm if the report is ready?"
        ),
        "mujhe samajh nahi aaya" to PhraseEntry(
            casual = "I didn't get that.",
            professional = "I apologize, could you please clarify?",
            alternative = "Could you explain that again?"
        ),
        "client ka call aaya tha" to PhraseEntry(
            casual = "The client called.",
            professional = "We received a call from the client.",
            alternative = "The client had reached out."
        ),
        "deadline kal hai" to PhraseEntry(
            casual = "The deadline is tomorrow.",
            professional = "The submission deadline is tomorrow.",
            alternative = "We need to deliver by tomorrow."
        ),
        "meeting postpone ho gayi" to PhraseEntry(
            casual = "The meeting got pushed back.",
            professional = "The meeting has been rescheduled.",
            alternative = "The meeting has been postponed."
        ),
        "internet nahi chal raha" to PhraseEntry(
            casual = "The internet is down.",
            professional = "We're experiencing connectivity issues.",
            alternative = "The network seems to be unavailable."
        ),
        "mujhe pata nahi" to PhraseEntry(
            casual = "I don't know.",
            professional = "I'm not sure about this. Let me check.",
            alternative = "I'll need to verify that."
        ),
        "kya hua" to PhraseEntry(
            casual = "What happened?",
            professional = "Could you brief me on the situation?",
            alternative = "What's going on?"
        ),
        "jaldi karo" to PhraseEntry(
            casual = "Hurry up!",
            professional = "Please expedite this.",
            alternative = "We need to move quickly on this."
        ),
        "kal aana" to PhraseEntry(
            casual = "Come tomorrow.",
            professional = "Please visit us tomorrow.",
            alternative = "We'd need you here tomorrow."
        ),
        "mera kaam ho gaya" to PhraseEntry(
            casual = "I'm done with my work.",
            professional = "I have completed my tasks.",
            alternative = "My part is done."
        ),
        "please thoda wait karo" to PhraseEntry(
            casual = "Please wait a moment.",
            professional = "Could you kindly hold on for a moment?",
            alternative = "Give me just a minute, please."
        ),
        "samajh nahi aaya bhai" to PhraseEntry(
            casual = "I don't follow, mate.",
            professional = "I'm afraid I didn't quite understand. Could you elaborate?",
            alternative = "Could you say that differently?"
        ),
        "mujhe help chahiye" to PhraseEntry(
            casual = "I need some help.",
            professional = "I would appreciate your assistance on this.",
            alternative = "Could you help me with something?"
        ),
        "abhi busy hun" to PhraseEntry(
            casual = "I'm busy right now.",
            professional = "I'm currently occupied. I'll get back to you shortly.",
            alternative = "I'm tied up at the moment."
        ),
        "kab miloge" to PhraseEntry(
            casual = "When can we meet?",
            professional = "When would you be available to meet?",
            alternative = "Could we schedule some time?"
        ),
        "shukriya yaar" to PhraseEntry(
            casual = "Thanks, buddy!",
            professional = "Thank you very much.",
            alternative = "I appreciate your help."
        ),
        "koi baat nahi" to PhraseEntry(
            casual = "No worries!",
            professional = "That's absolutely fine.",
            alternative = "Not a problem at all."
        ),
        "mujhe zyada kaam diya hai" to PhraseEntry(
            casual = "I've been given too much work.",
            professional = "My current workload is quite heavy. Could we reprioritize?",
            alternative = "I'm overloaded with tasks right now."
        ),
    )

    // ─────────────────────── Marathi Dictionary ───────────────────────
    private val marathiDictionary = mapOf(
        "udya meeting ahe ka" to PhraseEntry(
            casual = "Is there a meeting tomorrow?",
            professional = "Could you confirm if we have a meeting tomorrow?",
            alternative = "Is tomorrow's meeting happening?"
        ),
        "file pathavli ka" to PhraseEntry(
            casual = "Did you send the file?",
            professional = "Has the file been shared?",
            alternative = "Was the document forwarded?"
        ),
        "kaam jhale ka" to PhraseEntry(
            casual = "Is the work done?",
            professional = "Has the task been completed?",
            alternative = "Can you confirm if the work is finished?"
        ),
        "thoda vel thamba" to PhraseEntry(
            casual = "Give me a moment.",
            professional = "Could you please wait for a moment?",
            alternative = "I'll be right with you."
        ),
        "report tayar ahe ka" to PhraseEntry(
            casual = "Is the report ready?",
            professional = "Has the report been finalized?",
            alternative = "Can I have the report?"
        ),
        "mala nahi samajhle" to PhraseEntry(
            casual = "I didn't catch that.",
            professional = "Could you kindly elaborate?",
            alternative = "Would you mind repeating that?"
        ),
        "boss bolawat ahet" to PhraseEntry(
            casual = "The boss is calling you.",
            professional = "The manager is requesting your presence.",
            alternative = "You've been summoned by the manager."
        ),
        "kiti vel lagel" to PhraseEntry(
            meaning = "How long will it take",
            casual = "How long will it take?",
            professional = "Could you provide a timeline for this?",
            alternative = "What's the estimated time for completion?"
        ),
        "mala madath kara" to PhraseEntry(
            casual = "Please help me.",
            professional = "I would appreciate your assistance.",
            alternative = "Could you help me with this?"
        ),
        "mi yenar nahi" to PhraseEntry(
            casual = "I won't be coming.",
            professional = "I'm unable to attend.",
            alternative = "I'll have to skip this one."
        ),
    )

    fun translate(inputText: String, detectedLanguage: String): TranslationResult {
        val normalized = inputText.lowercase().trim()
        val context = detectContext(normalized)

        val phraseEntry = when {
            detectedLanguage.contains("Marathi", ignoreCase = true) ->
                findBestMatch(normalized, marathiDictionary)
            else ->
                findBestMatch(normalized, hindiDictionary)
        } ?: generateFallback(inputText)

        return TranslationResult(
            detectedInput = inputText,
            detectedLanguage = detectedLanguage,
            meaning = phraseEntry.meaning,
            casualSuggestion = phraseEntry.casual,
            professionalSuggestion = phraseEntry.professional,
            alternativeSuggestion = phraseEntry.alternative,
            context = context,
            confidence = calculateConfidence(normalized, detectedLanguage)
        )
    }

    private fun findBestMatch(text: String, dictionary: Map<String, PhraseEntry>): PhraseEntry? {
        // Exact match
        dictionary[text]?.let { return it }

        // Best partial match
        val inputWords = text.split(" ").toSet()
        var bestScore = 0.0
        var bestMatch: PhraseEntry? = null

        for ((phrase, entry) in dictionary) {
            val phraseWords = phrase.split(" ").toSet()
            val overlap = inputWords.intersect(phraseWords).size.toDouble()
            val score = overlap / maxOf(phraseWords.size, inputWords.size)
            if (score > bestScore && score >= 0.35) {
                bestScore = score
                bestMatch = entry
            }
        }
        return bestMatch
    }

    private fun generateFallback(text: String): PhraseEntry {
        return PhraseEntry(
            meaning = "Your statement",
                        casual = "\"$text\"",
            professional = "Could you rephrase this professionally?",
            alternative = "Try speaking clearly and I'll suggest English alternatives."
        )
    }

    fun detectContext(text: String): ConversationContext {
        val lower = text.lowercase()
        val workplaceCount = workplaceKeywords.count { it in lower }
        return when {
            workplaceCount >= 2 -> ConversationContext.FORMAL
            workplaceCount >= 1 -> ConversationContext.WORKPLACE
            else -> ConversationContext.CASUAL
        }
    }

    private fun calculateConfidence(text: String, language: String): Float {
        val dict = if (language.contains("Marathi", ignoreCase = true)) marathiDictionary else hindiDictionary
        val inputWords = text.split(" ").toSet()
        var maxScore = 0f
        for (phrase in dict.keys) {
            val phraseWords = phrase.split(" ").toSet()
            val overlap = inputWords.intersect(phraseWords).size.toFloat()
            val score = overlap / maxOf(phraseWords.size, inputWords.size)
            if (score > maxScore) maxScore = score
        }
        return minOf(maxScore * 1.2f, 1.0f)
    }
}
