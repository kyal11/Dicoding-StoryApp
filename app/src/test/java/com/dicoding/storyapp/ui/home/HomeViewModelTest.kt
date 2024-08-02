package com.dicoding.storyapp.ui.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.MainDispatcherRule
import com.dicoding.storyapp.StoryDataDummy
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.foundation.adapter.StoryDiffCallback
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var context: Context

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(storyRepository, userRepository, context)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = StoryDataDummy.generateDummyStoryResponse()
        val pagingData = PagingData.from(dummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = pagingData
        Mockito.`when`(storyRepository.getStories()).thenReturn(flowOf(pagingData))

        homeViewModel.getListStory()

        val actualStories: PagingData<ListStoryItem>? = homeViewModel.isListStory.asLiveData().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        actualStories?.let { differ.submitData(it) }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val pagingData = PagingData.from(emptyList<ListStoryItem>())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = pagingData
        Mockito.`when`(storyRepository.getStories()).thenReturn(flowOf(pagingData))

        homeViewModel.getListStory()

        val actualStories: PagingData<ListStoryItem>? = homeViewModel.isListStory.asLiveData().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        actualStories?.let { differ.submitData(it) }

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
