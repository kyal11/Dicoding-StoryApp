package com.dicoding.storyapp.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, StoryRemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao() : StoryDao
    abstract fun storyRemoteKeysDao() : StoryRemoteKeysDao
}