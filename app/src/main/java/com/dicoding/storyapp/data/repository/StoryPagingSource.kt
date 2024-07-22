package com.dicoding.storyapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.data.datasource.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getStories(page, params.loadSize)
            val stories = response.listStory
            Page(
                data = stories ,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}