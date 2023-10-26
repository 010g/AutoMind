package com.example.automind

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordFragmentUITest {

    // This rule provides functional testing of a single activity
    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun titleTextViewIsDisplayedWithCorrectText() {
        onView(withId(R.id.tv_tite))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_tite))
            .check(matches(withText("NEW NOTE")))
    }

    @Test
    fun sloganTextViewIsDisplayedWithCorrectText() {
        onView(withId(R.id.tv_slogan))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_slogan))
            .check(matches(withText("Record and Remember!")))
    }

    @Test
    fun micButtonIsDisplayed() {
        onView(withId(R.id.btn_mic))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadingAnimationIsInitiallyHidden() {
        // LottieAnimationView is initially hidden
        onView(withId(R.id.lottie_loading_animation))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
