package com.omarahmed.getnews.data

import com.omarahmed.getnews.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

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
        @Query("q") countryCode: String? = "my"
    ): Response<NewsResponse>
}