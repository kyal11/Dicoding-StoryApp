package com.dicoding.storyapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.dicoding.storyapp.JsonConverter
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiConfig
import com.dicoding.storyapp.foundation.utils.EspressoIdlingResource
import com.dicoding.storyapp.launchFragmentInHiltContainer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.API_TESTING = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        mockWebServer.shutdown()
        ApiConfig.API_TESTING = null
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStoriesHomeFragment_Success() {
        launchFragmentInHiltContainer<HomeFragment>()
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))

        mockWebServer.enqueue(mockResponse)


        Thread.sleep(3000)

        Espresso.onView(ViewMatchers.withText("testing by gallery uhuy"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}