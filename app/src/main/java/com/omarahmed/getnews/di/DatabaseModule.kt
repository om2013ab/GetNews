package com.omarahmed.getnews.di

import android.content.Context
import androidx.room.Room
import com.omarahmed.getnews.data.room.NewsDatabase
import com.omarahmed.getnews.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideNewDao(newsDatabase: NewsDatabase) = newsDatabase.newsDao()
}