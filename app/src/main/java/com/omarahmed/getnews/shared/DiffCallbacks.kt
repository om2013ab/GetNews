package com.omarahmed.getnews.shared

import androidx.recyclerview.widget.DiffUtil
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.models.Article

object DataItemsDiffCallback : DiffUtil.ItemCallback<DataItems>() {
    override fun areItemsTheSame(oldItem: DataItems, newItem: DataItems): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItems, newItem: DataItems): Boolean {
        return oldItem == newItem
    }
}

object ArticleDiffCallback: DiffUtil.ItemCallback<Article>(){
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
object SavedDiffCallback : DiffUtil.ItemCallback<SavedNewsEntity>() {
    override fun areItemsTheSame(oldItem: SavedNewsEntity, newItem: SavedNewsEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SavedNewsEntity, newItem: SavedNewsEntity): Boolean {
        return oldItem == newItem
    }
}
