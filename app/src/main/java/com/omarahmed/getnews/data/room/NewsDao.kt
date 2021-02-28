package com.omarahmed.getnews.data.room

import androidx.room.*
import com.omarahmed.getnews.data.room.entities.ForYouNewsEntity
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface NewsDao {
    //ForYouNews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForYouNews(forYouNewsEntity: ForYouNewsEntity)

    @Query("SELECT * FROM for_you_news_table ORDER BY id ASC")
    fun readForYouNews(): Flow<List<ForYouNewsEntity>>

    //LatestNews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLatestNews(latestNewsEntity: LatestNewsEntity)

    @Query("SELECT * FROM LATEST_NEWS_TABLE ORDER BY id ASC")
    fun readLatestNews(): Flow<List<LatestNewsEntity>>

    //SavedNews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedNews(savedNewsEntity: SavedNewsEntity)

    @Query("SELECT * FROM SAVED_NEWS_TABLE ORDER BY id ASC")
    fun readSavedNews(): Flow<List<SavedNewsEntity>>

    @Delete
    suspend fun deleteSavedNews(savedNewsEntity: SavedNewsEntity)
}