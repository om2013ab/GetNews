package com.omarahmed.getnews.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.omarahmed.getnews.databinding.ExploreHeaderLayoutBinding
import com.omarahmed.getnews.databinding.ExploreItemsBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.DataItems
import com.omarahmed.getnews.shared.DiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

const val HEADER_TYPE = 0
const val NEWS_TYPE = 1

class ExploreAdapter(val listener: ExploreAdapterInterface) : ListAdapter<DataItems, ViewHolder>(DiffCallback()) {
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
    inner class ExploreNewsViewHolder(val binding: ExploreItemsBinding) : ViewHolder(binding.root) {
        fun bindExplore(dataItems: DataItems.NewsItem) {
            binding.article = dataItems.article
            binding.executePendingBindings()
        }
    }

    inner class ExploreHeaderViewHolder(binding: ExploreHeaderLayoutBinding) : ViewHolder(binding.root) {
        init {
            listener.setupHeader(binding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_TYPE -> {
                val headerView = ExploreHeaderLayoutBinding.inflate(layoutInflater, parent, false)
                ExploreHeaderViewHolder(headerView)
            }
            NEWS_TYPE -> {
                val newsView = ExploreItemsBinding.inflate(layoutInflater, parent, false)
                ExploreNewsViewHolder(newsView)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ExploreNewsViewHolder -> {
                val currentNews = getItem(position) as DataItems.NewsItem
                holder.bindExplore(currentNews)
                holder.itemView.setOnClickListener {
                   val action = ExploreFragmentDirections.actionExploreFragmentToDetailsFragment(currentNews.article)
                    it.findNavController().navigate(action)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItems.Header -> HEADER_TYPE
            is DataItems.NewsItem -> NEWS_TYPE
        }
    }

    interface ExploreAdapterInterface {
        fun setupHeader(headerBinding: ExploreHeaderLayoutBinding)
    }

}