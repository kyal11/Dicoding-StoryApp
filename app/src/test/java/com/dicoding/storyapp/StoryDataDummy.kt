package com.dicoding.storyapp

import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem

object StoryDataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 1..10) {
            val story = ListStoryItem(
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2023-07-01T12:34:56Z",
                name = "User $i",
                description = "This is story description $i",
                lon = 106.8283 + i,
                id = "story-$i",
                lat = -6.1751 + i
            )
            items.add(story)
        }

        return items
    }
}
