package com.dicoding.storyapp.data.datasource.local.di

import android.content.Context
import androidx.room.Room
import com.dicoding.storyapp.data.datasource.local.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    @Singleton
    fun provideStoryDb(
        @ApplicationContext context: Context
    ): StoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "story_database"
        ).build()
    }

    @Provides
    fun provideStoryDao(database: StoryDatabase) = database.storyDao()
}