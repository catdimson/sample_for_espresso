package com.geekbrains.tests

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.search.MainActivity
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun activity_AssertNotNull() {
        scenario.onActivity { activity ->
            assertNotNull(activity)
        }
    }

    @Test
    fun activity_IsResumed() {
        scenario.onActivity {
            assertEquals(Lifecycle.State.RESUMED, it.lifecycle.currentState)
        }
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())

        if (BuildConfig.TYPE == MainActivity.FAKE) {
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 42")))
        } else {
            onView(isRoot()).perform(delay())
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 2283")))
        }
    }

    @Test
    fun activityEditText_NotNull() {
        onView(withId(R.id.searchEditText))
            .check(matches(withText("")))
    }

    @Test
    fun activityEditText_IsDisplayed() {
        val assertion = matches(isDisplayed())
        onView(withId(R.id.searchEditText)).check(assertion)
    }

    @Test
    fun activityEditText_IsDisplayingAtLeast() {
        val assertion = matches(isDisplayingAtLeast(100))
        onView(withId(R.id.searchEditText)).check(assertion)
    }

    @Test
    fun activityEditText_HasText() {
        val assertionText = matches(withText("input data"))
        val assertionClear = matches(withText(""))
        onView(withId(R.id.searchEditText))
            .perform(click())
            .perform(typeText("input data"))
            .check(assertionText)
            .perform(clearText())
            .check(assertionClear)
    }

    @Test
    fun activityButton_IsDisplayed() {
        val assertion = matches(isDisplayed())
        onView(withId(R.id.toDetailsActivityButton)).check(assertion)
    }

    @Test
    fun activityButton_WithEffectiveVisibility() {
        val assertion = matches(withEffectiveVisibility(Visibility.VISIBLE))
        onView(withId(R.id.toDetailsActivityButton)).check(assertion)
    }

    @Test
    fun activityButton_Intent() {
        onView(withId(R.id.toDetailsActivityButton))
            .perform(click())
        onView(withId(R.id.totalCountTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText("Number of results: 0")))
    }

    @After
    fun close() {
        scenario.close()
    }

    private fun delay(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $2 seconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(2000)
            }
        }
    }
}
