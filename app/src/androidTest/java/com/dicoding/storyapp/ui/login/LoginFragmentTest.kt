package com.dicoding.storyapp.ui.login


import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.storyapp.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.dicoding.storyapp.foundation.utils.EspressoIdlingResource
import com.dicoding.storyapp.launchFragmentInHiltContainer

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLogin() {
        launchFragmentInHiltContainer<LoginFragment>()

        onView(withId(R.id.editTextEmailCustomLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextEmailCustomLogin)).perform(ViewActions.typeText("rizky@gmail.com"))

        onView(withId(R.id.editTextPasswordCustomLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPasswordCustomLogin)).perform(ViewActions.typeText("rizky123"))

        closeSoftKeyboard()

        onView(withId(R.id.btn_signin_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_signin_login))
            .perform(ViewActions.click())
    }
}
