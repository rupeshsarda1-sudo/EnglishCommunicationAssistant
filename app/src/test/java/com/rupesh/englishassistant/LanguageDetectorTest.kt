package com.rupesh.englishassistant

import com.rupesh.englishassistant.service.LanguageDetector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LanguageDetectorTest {

    private lateinit var detector: LanguageDetector

    @Before
    fun setUp() {
        detector = LanguageDetector()
    }

    @Test
    fun `detect Hindi from romanized text`() {
        val lang = detector.detectByRomanizedKeywords("kal meeting hai kya")
        assertEquals("Hindi", lang)
    }

    @Test
    fun `detect Marathi from romanized text`() {
        val lang = detector.detectByRomanizedKeywords("udya meeting ahe ka")
        assertEquals("Marathi", lang)
    }

    @Test
    fun `detect Hinglish from mixed text`() {
        val lang = detector.detectByRomanizedKeywords("please meeting confirm karo")
        assertEquals("Hindi", lang)
    }

    @Test
    fun `detect Marathi over Hindi when Marathi words present`() {
        val lang = detector.detectByRomanizedKeywords("kaam jhale ka mala nahi samajhle")
        assertEquals("Marathi", lang)
    }

    @Test
    fun `returns Hinglish Mixed for unrecognized text`() {
        val lang = detector.detectByRomanizedKeywords("hello world good morning")
        assertEquals("Hinglish/Mixed", lang)
    }

    @Test
    fun `handles empty string gracefully`() {
        val lang = detector.detectByRomanizedKeywords("")
        assertNotNull(lang)
    }

    @Test
    fun `handles single word input`() {
        val lang = detector.detectByRomanizedKeywords("hai")
        assertEquals("Hindi", lang)
    }

    @Test
    fun `handles all marathi keywords`() {
        val lang = detector.detectByRomanizedKeywords("thamba mala tayar ahe")
        assertEquals("Marathi", lang)
    }
}
