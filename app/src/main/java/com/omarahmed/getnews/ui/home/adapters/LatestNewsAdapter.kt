package com.omarahmed.getnews.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.LatestNewsRowBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.ui.home.HomeFragmentDirections
import com.omarahmed.getnews.ui.home.adapters.LatestNewsAdapter.HeaderViewHolder.Companion.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_HEADER_TYPE = 0
private const val ITEM_NEWS_TYPE = 1

class LatestNewsAdapter (
        private val listener: OnSavedClickListener
        ):
    androidx.recyclerview.widget.ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {
    object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<Article>?) {
        adapterScope.launch {
            val item = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map {
                    DataItem.NewsItem(it, itemCount)
                }
            }
            withContext(Dispatchers.Main) {
                submitList(item)
            }
        }

    }

    inner class LatestNewsViewHolder(var binding: LatestNewsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: DataItem.NewsItem) {
            binding.article = dataItem.article
            binding.executePendingBindings()
        }
        init {
            binding.ivLatestNewsSave.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION){
                    val currentNews = getItem(adapterPosition) as DataItem.NewsItem
                    listener.onSavedClick(currentNews.article)
                }

            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.latest_news_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER_TYPE -> from(parent)
            ITEM_NEWS_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = LatestNewsRowBinding.inflate(layoutInflater, parent, false)
                return LatestNewsViewHolder(view)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LatestNewsViewHolder -> {
                val currentNews = getItem(position) as DataItem.NewsItem
                holder.bind(currentNews)
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(currentNews.article)
                holder.itemView.setOnClickListener {
                    it.findNavController().navigate(action)
                }
            }

        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_HEADER_TYPE
            is DataItem.NewsItem -> ITEM_NEWS_TYPE
        }
    }
    interface OnSavedClickListener{
        fun onSavedClick(article: Article)
    }
}

sealed class DataItem {
    abstract val id: Int
    data class NewsItem(val article: Article, override val id: Int) : DataItem()

    object Header : DataItem() {
        override val id: Int
            get() = Int.MIN_VALUE
    }
}

