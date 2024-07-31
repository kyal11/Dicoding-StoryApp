package com.dicoding.storyapp.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<StoryRemoteKeys>)

    @Query("SELECT * FROM story_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysById(id: String): StoryRemoteKeys?

    @Query("DELETE FROM story_remote_keys")
    suspend fun deleteAll()
}