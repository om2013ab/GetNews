package com.omarahmed.getnews.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.util.Constants.SAVED_NEWS_TABLE

@Entity(tableName = SAVED_NEWS_TABLE)
data class SavedNewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val article: Article
)