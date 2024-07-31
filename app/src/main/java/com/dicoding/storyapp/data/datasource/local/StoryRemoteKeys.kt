package com.dicoding.storyapp.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_remote_keys")
data class StoryRemoteKeys (
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)