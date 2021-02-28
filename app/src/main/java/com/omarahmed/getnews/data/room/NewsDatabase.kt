package com.omarahmed.getnews.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.omarahmed.getnews.data.NewsTypeConverter
import com.omarahmed.getnews.data.room.entities.ForYouNewsEntity
import com.omarahmed.getnews.data.room.entities.LatestNewsEntity
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity

@Database(
    entities = [ForYouNewsEntity::class, LatestNewsEntity::class, SavedNewsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsTypeConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}