package com.omarahmed.getnews.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omarahmed.getnews.models.NewsResponse
import com.omarahmed.getnews.util.Constants.FOR_YOU_NEWS_TABLE

@Entity(tableName = FOR_YOU_NEWS_TABLE)
data class ForYouNewsEntity(
    val newsResponse: NewsResponse
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
