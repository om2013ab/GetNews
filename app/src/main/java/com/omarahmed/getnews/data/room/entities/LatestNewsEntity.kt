package com.omarahmed.getnews.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.Constants.LATEST_NEWS_TABLE

@Entity(tableName = LATEST_NEWS_TABLE)
data class LatestNewsEntity(
    val newsResponse: NewsResponse
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
