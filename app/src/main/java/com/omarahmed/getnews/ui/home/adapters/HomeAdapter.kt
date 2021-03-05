package com.omarahmed.getnews.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.LatestNewsHeaderBinding
import com.omarahmed.getnews.databinding.LatestNewsRowBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.DataItems
import com.omarahmed.getnews.shared.DiffCallback
import com.omarahmed.getnews.ui.home.HomeFragmentDirections
import com.omarahmed.getnews.ui.saved.SavedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_HEADER_TYPE = 0
private const val ITEM_NEWS_TYPE = 1

class HomeAdapter(
        private val listener: HomeAdapterInterface,
        val fragment: FragmentActivity
) : androidx.recyclerview.widget.ListAdapter<DataItems, RecyclerView.ViewHolder>(DiffCallback()) {
    private var newsSaved = false
    private var newsSavedId = 0
    private val savedNewsViewMode = ViewModelProvider(fragment).get(SavedViewModel::class.java)

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<Article>?) {
        adapterScope.launch {
            val item = when (list) {
                null -> listOf(DataItems.Header)
                else -> listOf(DataItems.Header) + list.map {
                    DataItems.NewsItem(it, itemCount)
                }
            }
            withContext(Dispatchers.Main) {
                submitList(item)
            }
        }

    }

    inner class NewsViewHolder(val binding: LatestNewsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: DataItems.NewsItem) {
            binding.article = dataItem.article
            binding.executePendingBindings()
        }
        init {
            binding.ivLatestNewsSave.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION){
                    val currentNews = getItem(adapterPosition) as DataItems.NewsItem
                    if (!newsSaved){
                        listener.onSavedClick(currentNews.article, binding.ivLatestNewsSave)
                        newsSaved = true
                    } else {
                        listener.onUnSavedClick(0,currentNews.article, binding.ivLatestNewsSave)
                        newsSaved = false
                    }
                }
            }

        }
    }

    inner class HeaderViewHolder(headerBinding: LatestNewsHeaderBinding) : RecyclerView.ViewHolder(headerBinding.root) {
        init {
            listener.setupHeader(headerBinding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_HEADER_TYPE -> {
                val headerView = LatestNewsHeaderBinding.inflate(layoutInflater, parent, false)
                HeaderViewHolder(headerView)
            }
            ITEM_NEWS_TYPE -> {
                val newsView = LatestNewsRowBinding.inflate(layoutInflater, parent, false)
                NewsViewHolder(newsView)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> {
                val currentNews = getItem(position) as DataItems.NewsItem
                holder.bind(currentNews)
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(currentNews.article)
                holder.itemView.setOnClickListener {
                    it.findNavController().navigate(action)
                }
                savedNewsViewMode.readSavedNews.observe(fragment) { savedNews ->
                    savedNews.forEach { news ->
                        if (news.article.url == currentNews.article.url) {
                            newsSavedId = news.id
                            newsSaved = true
                            holder.binding.ivLatestNewsSave.setImageResource(R.drawable.ic_bookmark_saved)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItems.Header -> ITEM_HEADER_TYPE
            is DataItems.NewsItem -> ITEM_NEWS_TYPE
        }
    }

    interface HomeAdapterInterface {
        fun onSavedClick(article: Article, imageView: ImageView)
        fun onUnSavedClick(newsId: Int, article: Article, imageView: ImageView)
        fun setupHeader(headerBinding: LatestNewsHeaderBinding)
    }
}

