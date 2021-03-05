package com.omarahmed.getnews.data

import com.omarahmed.getnews.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/top-headlines")
    suspend fun getForYouNews(
            @Query("apiKey") apiKey: String,
            @Query("language") language: String? = "en",
            @Query("q") country: String? = "Malaysia"
    ): Response<NewsResponse>

    @GET("/v2/top-headlines")
    suspend fun getLatestNews(
            @Query("apiKey") apiKey: String,
            @Query("language") language: String? = "en",
    ): Response<NewsResponse>


    @GET("/v2/top-headlines")
    suspend fun getExploreNews(
            @Query("apiKey") apiKey: String,
            @Query("category") category: String,
            @Query("language") language: String? = "en"
    ): Response<NewsResponse>
}