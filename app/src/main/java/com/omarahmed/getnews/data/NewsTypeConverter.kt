package com.omarahmed.getnews.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.models.NewsResponse

class NewsTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun newsResponseToString(newsResponse: NewsResponse): String = gson.toJson(newsResponse)

    @TypeConverter
    fun stringToNewsResponse(data: String): NewsResponse{
        val listType = object : TypeToken<NewsResponse>(){}.type
        return gson.fromJson(data,listType)
    }
    @TypeConverter
    fun articleToString(article: Article): String = gson.toJson(article)

    @TypeConverter
    fun stringToArticle(data: String): Article {
        val listType = object : TypeToken<Article>(){}.type
        return gson.fromJson(data,listType)
    }
}