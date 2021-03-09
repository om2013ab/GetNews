package com.omarahmed.getnews.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omarahmed.getnews.databinding.ExploreItemsBinding
import com.omarahmed.getnews.databinding.SearchNewsItemsBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.ui.search.SearchAdapter.SearchViewHolder

class SearchAdapter: ListAdapter<Article, SearchViewHolder>(DiffCallback) {

    object DiffCallback: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    class SearchViewHolder(val binding: SearchNewsItemsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article){
            binding.article = article
            binding.executePendingBindings()
        }
        fun setNavigation(article: Article){
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(article)
            itemView.setOnClickListener {
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = SearchNewsItemsBinding.inflate(inflater,parent,false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.setNavigation(getItem(position))
    }
}