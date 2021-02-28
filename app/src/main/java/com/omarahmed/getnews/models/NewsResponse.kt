package com.omarahmed.getnews.models


import com.google.gson.annotations.SerializedName
import com.omarahmed.getnews.models.Article

data class NewsResponse(
    @SerializedName("articles")
    val articles: List<Article>,
)