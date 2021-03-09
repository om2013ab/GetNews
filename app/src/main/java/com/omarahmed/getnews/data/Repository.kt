package com.omarahmed.getnews.data

import com.omarahmed.getnews.data.room.entities.ForYouNewsEntity
import com.omarahmed.getnews.data.room.NewsDao
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
        private val newsApi: NewsApi,
        private val newsDao: NewsDao
) {

    /** REMOTE*/
    suspend fun getForYouNewsFromRemote(apiKey: String) =
            newsApi.getForYouNews(apiKey)

    suspend fun getLatestNewsFromApi(apiKey: String) = newsApi.getLatestNews(apiKey)
    suspend fun getExploreNews(apiKey: String, category: String) = newsApi.getExploreNews(apiKey, category)
    suspend fun getSearchNews(apiKey: String, query: String) = newsApi.getSearchNews(apiKey,query)


    /** LOCAL*/

    //ForYouNews
    fun readForYouNews() = newsDao.readForYouNews()
    suspend fun insertForYouNews(forYouNewsEntity: ForYouNewsEntity) =
            newsDao.insertForYouNews(forYouNewsEntity)

    //LatestNews
    fun readLatestNews() = newsDao.readLatestNews()
    suspend fun insertLatestNews(latestNewsEntity: LatestNewsEntity) =
            newsDao.insertLatestNews(latestNewsEntity)

    //SavedNews
    fun readSavedNews() = newsDao.readSavedNews()
    suspend fun insertSavedNews(savedNewsEntity: SavedNewsEntity) =
            newsDao.insertSavedNews(savedNewsEntity)

    suspend fun deleteSavedNews(savedNewsEntity: SavedNewsEntity) =
            newsDao.deleteSavedNews(savedNewsEntity)
}