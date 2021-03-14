package com.omarahmed.getnews.shared

import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.models.Article

class ShareClickListener(val clickListener: (url: String)-> Unit) {

    fun shareNewsLink(article: Article) = clickListener(article.url)
}

class UnsavedClickListener(val unsavedClickListener: (savedNewsEntity: SavedNewsEntity) -> Unit){
    fun unSavedNews(savedEntity: SavedNewsEntity) = unsavedClickListener(savedEntity)
}