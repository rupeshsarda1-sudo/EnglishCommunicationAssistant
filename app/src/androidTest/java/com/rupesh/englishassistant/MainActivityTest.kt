package com.rupesh.englishassistant

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun app_launches_successfully() {
        onView(withId(R.id.fabMic)).check(matches(isDisplayed()))
    }

    @Test
    fun status_message_is_visible_on_launch() {
        onView(withId(R.id.tvStatusMessage)).check(matches(isDisplayed()))
    }

    @Test
    fun type_input_button_is_visible() {
        onView(withId(R.id.btnTypedInput)).check(matches(isDisplayed()))
    }

    @Test
    fun typed_input_dialog_opens_on_click() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withText("Translate")).check(matches(isDisplayed()))
    }

    @Test
    fun result_card_hidden_on_launch() {
        onView(withId(R.id.cardResult)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun typed_hindi_input_shows_result() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("kal meeting hai kya confirm nahi hai"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500) // Wait for async processing
        onView(withId(R.id.cardResult)).check(matches(isDisplayed()))
    }

    @Test
    fun typed_marathi_input_shows_result() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("udya meeting ahe ka"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.cardResult)).check(matches(isDisplayed()))
    }

    @Test
    fun result_shows_casual_and_professional_suggestions() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("tumne file bheja kya"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.tvCasualSuggestion)).check(matches(isDisplayed()))
        onView(withId(R.id.tvProfessionalSuggestion)).check(matches(isDisplayed()))
    }

    @Test
    fun reset_button_visible_after_result() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("kaam ho gaya kya"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.btnReset)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun reset_button_hides_result_card() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("deadline kal hai"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.btnReset)).perform(click())
        onView(withId(R.id.cardResult)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun detected_language_label_shown_in_result() {
        onView(withId(R.id.btnTypedInput)).perform(click())
        onView(withHint("Type in Hindi/Marathi/Hinglish...")).perform(
            typeText("boss ne bulaya hai"), closeSoftKeyboard()
        )
        onView(withText("Translate")).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.tvDetectedLanguage)).check(matches(isDisplayed()))
    }
}
