package com.omarahmed.getnews.shared

import com.omarahmed.getnews.models.Article

sealed class DataItems {
    abstract val id: Int

    data class NewsItem(val article: Article, override val id: Int) : DataItems()

    object Header : DataItems() {
        override val id: Int
            get() = Int.MIN_VALUE
    }
}

