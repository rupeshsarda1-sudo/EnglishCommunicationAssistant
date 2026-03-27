package com.rupesh.englishassistant

import com.rupesh.englishassistant.model.ConversationContext
import com.rupesh.englishassistant.service.TranslationEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TranslationEngineTest {

    private lateinit var engine: TranslationEngine

    @Before
    fun setUp() {
        engine = TranslationEngine()
    }

    // ── Hindi Tests ─────────────────────────────────────────
    @Test
    fun `translate meeting confirmation Hindi returns correct casual`() {
        val result = engine.translate("kal meeting hai kya confirm nahi hai", "Hindi")
        assertEquals("Is tomorrow's meeting confirmed?", result.casualSuggestion)
    }

    @Test
    fun `translate file sent Hindi returns professional suggestion`() {
        val result = engine.translate("tumne file bheja kya", "Hindi")
        assertEquals("Have you shared the file?", result.professionalSuggestion)
    }

    @Test
    fun `translate boss called Hindi returns correct output`() {
        val result = engine.translate("boss ne bulaya hai", "Hindi")
        assertTrue(result.casualSuggestion.isNotEmpty())
        assertTrue(result.professionalSuggestion.isNotEmpty())
    }

    @Test
    fun `translate deadline Hindi returns non-empty result`() {
        val result = engine.translate("deadline kal hai", "Hindi")
        assertEquals("Hindi", result.detectedLanguage)
        assertNotNull(result.casualSuggestion)
        assertTrue(result.casualSuggestion.isNotEmpty())
    }

    @Test
    fun `translate meeting postponed Hindi`() {
        val result = engine.translate("meeting postpone ho gayi", "Hindi")
        assertTrue(result.casualSuggestion.contains("meeting", ignoreCase = true) ||
                   result.professionalSuggestion.contains("meeting", ignoreCase = true))
    }

    @Test
    fun `translate help needed Hindi`() {
        val result = engine.translate("mujhe help chahiye", "Hindi")
        assertTrue(result.casualSuggestion.contains("help", ignoreCase = true))
    }

    @Test
    fun `translate internet down Hindi`() {
        val result = engine.translate("internet nahi chal raha", "Hindi")
        assertTrue(result.casualSuggestion.isNotEmpty())
        assertTrue(result.professionalSuggestion.isNotEmpty())
    }

    // ── Marathi Tests ────────────────────────────────────────
    @Test
    fun `translate meeting Marathi returns correct casual`() {
        val result = engine.translate("udya meeting ahe ka", "Marathi")
        assertEquals("Is there a meeting tomorrow?", result.casualSuggestion)
    }

    @Test
    fun `translate file Marathi returns professional`() {
        val result = engine.translate("file pathavli ka", "Marathi")
        assertEquals("Has the file been shared?", result.professionalSuggestion)
    }

    @Test
    fun `translate work done Marathi`() {
        val result = engine.translate("kaam jhale ka", "Marathi")
        assertEquals("Is the work done?", result.casualSuggestion)
    }

    @Test
    fun `translate boss calling Marathi`() {
        val result = engine.translate("boss bolawat ahet", "Marathi")
        assertTrue(result.professionalSuggestion.contains("manager", ignoreCase = true))
    }

    // ── Context Detection Tests ───────────────────────────────
    @Test
    fun `detect workplace context for meeting`() {
        val context = engine.detectContext("kal meeting hai")
        assertEquals(ConversationContext.WORKPLACE, context)
    }

    @Test
    fun `detect workplace context for report`() {
        val context = engine.detectContext("report ready hai kya")
        assertEquals(ConversationContext.WORKPLACE, context)
    }

    @Test
    fun `detect casual context for general phrase`() {
        val context = engine.detectContext("kaise ho yaar")
        assertEquals(ConversationContext.CASUAL, context)
    }

    @Test
    fun `detect formal context with multiple workplace keywords`() {
        val context = engine.detectContext("boss ke saath meeting aur client report deadline kal hai")
        assertEquals(ConversationContext.FORMAL, context)
    }

    // ── Result Structure Tests ────────────────────────────────
    @Test
    fun `result has all required fields populated`() {
        val result = engine.translate("kaam ho gaya kya", "Hindi")
        assertNotNull(result.detectedInput)
        assertNotNull(result.detectedLanguage)
        assertNotNull(result.casualSuggestion)
        assertNotNull(result.professionalSuggestion)
        assertTrue(result.confidence >= 0f)
        assertTrue(result.confidence <= 1f)
    }

    @Test
    fun `empty input returns fallback result`() {
        val result = engine.translate("xyzabc blah", "Hindi")
        assertNotNull(result)
        assertNotNull(result.casualSuggestion)
    }

    @Test
    fun `partial match works for similar phrases`() {
        val result = engine.translate("meeting postpone hui", "Hindi")
        assertNotNull(result.casualSuggestion)
        assertTrue(result.casualSuggestion.isNotEmpty())
    }
}
